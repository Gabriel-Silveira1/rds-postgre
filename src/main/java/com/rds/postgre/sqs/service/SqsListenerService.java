package com.rds.postgre.sqs.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rds.postgre.cepregistrado.CepRegistradoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.*;

import java.util.List;

@Service
public class SqsListenerService {

    private static final Logger log = LoggerFactory.getLogger(SqsListenerService.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    private final SqsClient sqsClient;
    private final CepRegistradoRepository repository;

    @Value("${aws.sqs.queue-url}")
    private String queueUrl;

    public SqsListenerService(SqsClient sqsClient, CepRegistradoRepository repository) {
        this.sqsClient = sqsClient;
        this.repository = repository;
    }

    @Scheduled(fixedDelay = 5000)
    public void pollQueue() {
        ReceiveMessageRequest request = ReceiveMessageRequest.builder()
                .queueUrl(queueUrl)
                .maxNumberOfMessages(10)
                .waitTimeSeconds(5)
                .build();

        List<Message> mensagens = sqsClient.receiveMessage(request).messages();

        for (Message mensagem : mensagens) {
            try {
                String cep = extrairCep(mensagem.body());
                repository.salvar(cep);
                log.info("CEP {} registrado via SQS", cep);

                sqsClient.deleteMessage(DeleteMessageRequest.builder()
                        .queueUrl(queueUrl)
                        .receiptHandle(mensagem.receiptHandle())
                        .build());

            } catch (Exception e) {
                log.error("Erro ao processar mensagem SQS: {}", e.getMessage());
            }
        }
    }

    private String extrairCep(String corpoMensagem) throws Exception {
        JsonNode json = objectMapper.readTree(corpoMensagem);
        if (json.has("Message")) {
            return json.get("Message").asText().trim();
        }
        return corpoMensagem.trim();
    }
}