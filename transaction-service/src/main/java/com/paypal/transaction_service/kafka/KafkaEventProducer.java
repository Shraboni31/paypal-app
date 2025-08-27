package com.paypal.transaction_service.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
public class KafkaEventProducer {

    private static final String TOPIC = "txn-initiated";
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public KafkaEventProducer(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
        //Register module to handle Java 8 date/time serialization
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    //send raw string message
    public void sendTransactionEvent(String key, String message){
        System.out.println("Sending to kafka -> Topic: "+TOPIC +", key: "+key+ ", message: "+message);
        CompletableFuture<SendResult<String, String>> future = kafkaTemplate.send(TOPIC, key, message);

        future.thenAccept(result ->{
            RecordMetadata metadata = result.getRecordMetadata();
            System.out.println(" Kafka message sent successfully! Topic: "+metadata.topic()+" ,Partition: "+metadata.partition());
        }).exceptionally(ex->{
            System.out.println("Failed to send kafka message: "+ex.getMessage());
            return null;
        });
    }
}
