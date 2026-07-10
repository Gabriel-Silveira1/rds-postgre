package com.rds.postgre.sqs.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SqsService {

    private static final Logger log = LoggerFactory.getLogger(SqsService.class);

    private final SqsClient sqsClient;

    @Value("${aws.sqs.queue-url}")
    private String queueUrl;

    public SqsService(SqsClient sqsClient) {
        this.sqsClient = sqsClient;
    }

    public String enviarMensagem(String mensagem) {
        SendMessageRequest request = SendMessageRequest.builder()
                .queueUrl(queueUrl)
                .messageBody(mensagem)
                .build();

        SendMessageResponse response = sqsClient.sendMessage(request);
        log.info("Mensagem enviada direto pra fila, MessageId: {}", response.messageId());
        return response.messageId();
    }

    public List<String> visualizarMensagens() {
        ReceiveMessageRequest request = ReceiveMessageRequest.builder()
                .queueUrl(queueUrl)
                .maxNumberOfMessages(10)
                .waitTimeSeconds(2)
                .build();

        List<Message> mensagens = sqsClient.receiveMessage(request).messages();
        // Nota: isso "toca" nas mensagens (elas ficam invisíveis pelo visibility timeout),
        // mas não deleta -- é só uma visualização, não interfere no SqsListenerService.
        return mensagens.stream()
                .map(Message::body)
                .collect(Collectors.toList());
    }

    public int contarMensagens() {
        GetQueueAttributesRequest request = GetQueueAttributesRequest.builder()
                .queueUrl(queueUrl)
                .attributeNames(QueueAttributeName.APPROXIMATE_NUMBER_OF_MESSAGES)
                .build();

        GetQueueAttributesResponse response = sqsClient.getQueueAttributes(request);
        return Integer.parseInt(response.attributes().get(QueueAttributeName.APPROXIMATE_NUMBER_OF_MESSAGES));
    }

    public void limparFila() {
        PurgeQueueRequest request = PurgeQueueRequest.builder()
                .queueUrl(queueUrl)
                .build();

        sqsClient.purgeQueue(request);
        log.warn("Fila purgada manualmente via endpoint");
    }

    public List<Map<String, String>> visualizarComHandle() {
        ReceiveMessageRequest request = ReceiveMessageRequest.builder()
                .queueUrl(queueUrl)
                .maxNumberOfMessages(10)
                .waitTimeSeconds(2)
                .build();

        List<Message> mensagens = sqsClient.receiveMessage(request).messages();

        return mensagens.stream()
                .map(m -> Map.of(
                        "corpo", m.body(),
                        "receiptHandle", m.receiptHandle()
                ))
                .collect(Collectors.toList());
    }

    public void removerMensagem(String receiptHandle) {
        sqsClient.deleteMessage(DeleteMessageRequest.builder()
                .queueUrl(queueUrl)
                .receiptHandle(receiptHandle)
                .build());
        log.info("Mensagem removida manualmente da fila");
    }

    public Map<String, String> chamarProximo() {
        ReceiveMessageRequest request = ReceiveMessageRequest.builder()
                .queueUrl(queueUrl)
                .maxNumberOfMessages(1)
                .waitTimeSeconds(2)
                .build();

        List<Message> mensagens = sqsClient.receiveMessage(request).messages();

        if (mensagens.isEmpty()) {
            return Map.of("status", "fila vazia");
        }

        Message proxima = mensagens.get(0);
        removerMensagem(proxima.receiptHandle());

        return Map.of(
                "status", "atendido e removido",
                "conteudo", proxima.body()
        );
    }
}