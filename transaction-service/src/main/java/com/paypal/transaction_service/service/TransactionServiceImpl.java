package com.paypal.transaction_service.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.paypal.transaction_service.entity.Transaction;
import com.paypal.transaction_service.kafka.KafkaEventProducer;
import com.paypal.transaction_service.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TransactionServiceImpl implements TransactionService{

    private final TransactionRepository transactionRepository;
    private final ObjectMapper objectMapper;
    private final KafkaEventProducer kafkaEventProducer;

    public TransactionServiceImpl(TransactionRepository transactionRepository, ObjectMapper objectMapper, KafkaEventProducer kafkaEventProducer) {
        this.transactionRepository = transactionRepository;
        this.objectMapper = objectMapper;
        this.kafkaEventProducer = kafkaEventProducer;
    }

    @Override
    public Transaction createTransaction(Transaction transactionRequest) {
        Transaction transaction = new Transaction();
        transaction.setSenderId(transactionRequest.getSenderId());
        transaction.setReceiverId(transactionRequest.getReceiverId());
        transaction.setAmount(transactionRequest.getAmount());
        transaction.setTimestamp(LocalDateTime.now());
        transaction.setStatus("SUCCESS");
        Transaction saved = transactionRepository.save(transaction);
        System.out.println("Saved transaction from DB : "+saved);

        //Publishing kafka event
        try{
            String eventPayLoad = objectMapper.writeValueAsString(saved);//converts the Java object into a JSON string.
            String key = String.valueOf(saved.getId());
            kafkaEventProducer.sendTransactionEvent(key, eventPayLoad);
            System.out.println("Kafka message sent");
        }catch(Exception ex){
            System.out.println("Failed to send kafka event: "+ex.getMessage());
            ex.printStackTrace();
        }
        return saved;
    }

    @Override
    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }
}
