package com.rds.postgre.sns.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.*;

@Service
public class SnsService {

    private static final Logger log = LoggerFactory.getLogger(SnsService.class);

    private final SnsClient snsClient;

    @Value("${aws.sns.topic-arn}")
    private String topicArn;

    public SnsService(SnsClient snsClient) {
        this.snsClient = snsClient;
    }

    public String publish(String message) {
        PublishRequest request = PublishRequest.builder()
                .topicArn(topicArn)
                .message(message)
                .build();

        PublishResponse response = snsClient.publish(request);
        log.info("Mensagem publicada, MessageId: {}", response.messageId());
        return response.messageId();
    }

    public String subscribeEmail(String email) {
        SubscribeRequest request = SubscribeRequest.builder()
                .topicArn(topicArn)
                .protocol("email")
                .endpoint(email)
                .build();

        SubscribeResponse response = snsClient.subscribe(request);
        log.info("Inscrição por e-mail criada para: {}", email);
        return "Inscrição criada. ARN: " + response.subscriptionArn() +
                " (verifique seu e-mail e clique em 'Confirm subscription')";
    }
}