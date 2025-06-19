package com.bank.devteam.service;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bank.devteam.model.CdersCrdConsumerData;
import com.bank.devteam.model.KafkaConsumerConfigEntity;
import com.bank.devteam.repository.CdersCrdConsumerDataRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaConsumerService {

 private final CdersCrdConsumerDataRepository cdersCrdConsumerDataRepository;
 private final PostgresProcessorService postgresProcessorService;
 private final ObjectMapper objectMapper; // For JSON parsing

 @Transactional // Ensure atomicity of insertion and function call
 public void processMessage(String message, KafkaConsumerConfigEntity consumerConfig) {
     log.info("Received message for topic {}: {}", consumerConfig.getTopicName(), message);

     CdersCrdConsumerData consumerData = new CdersCrdConsumerData();
     try {
         JsonNode jsonNode = objectMapper.readTree(message);
         consumerData.setJsonData(jsonNode);
     } catch (Exception e) {
         log.error("Error parsing JSON message for topic {}: {}", consumerConfig.getTopicName(), e.getMessage());
         // Depending on your error handling strategy, you might want to
         // dead-letter queue this message or throw an exception to reprocess.
         throw new RuntimeException("Failed to parse JSON message", e);
     }

     try {
         // 1. Insert complete message to cders_crd_consumer_data
         CdersCrdConsumerData savedData = cdersCrdConsumerDataRepository.save(consumerData);
         log.info("Message for topic {} inserted with msg_id: {}", consumerConfig.getTopicName(), savedData.getMsgId());

         // 2. Call PostgreSQL function
         postgresProcessorService.callPostgresFunction(consumerConfig.getFunctionName(), savedData.getMsgId());

         log.info("Message for topic {} processed successfully, msg_id: {}", consumerConfig.getTopicName(), savedData.getMsgId());
     } catch (DataIntegrityViolationException e) {
         log.error("Data integrity violation when processing message for topic {}: {}", consumerConfig.getTopicName(), e.getMessage());
         // Handle duplicate keys, etc.
         throw new RuntimeException("Data integrity error", e);
     } catch (Exception e) {
         log.error("Error processing message for topic {}: {}", consumerConfig.getTopicName(), e.getMessage(), e);
         // Re-throw to trigger Kafka re-delivery if using manual acknowledgment and error handling
         throw new RuntimeException("Failed to process Kafka message", e);
     }
 }
}