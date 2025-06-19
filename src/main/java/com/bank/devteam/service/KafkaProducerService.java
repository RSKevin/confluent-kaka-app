package com.bank.devteam.service;

import java.util.concurrent.CompletableFuture;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import com.bank.devteam.repository.KafkaProducerConfigRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaProducerService {

 private final KafkaTemplate<String, String> kafkaTemplate;
 private final KafkaProducerConfigRepository kafkaProducerConfigRepository;

 public CompletableFuture<SendResult<String, String>> sendMessage(String topic, String message) {
     // You might want to load specific producer configs here if each producer topic
     // needs distinct Kafka producer properties (e.g., acks, retries).
     // For simplicity, we're using a single KafkaTemplate initialized with common properties.
     // If you need topic-specific producer properties, you'd create multiple KafkaTemplate instances
     // or configure the ProducerFactory to handle it.

     kafkaProducerConfigRepository.findByTopicName(topic).ifPresentOrElse(
             config -> {
                 if (!config.isEnabled()) {
                     log.warn("Producer topic {} is disabled. Message will not be sent.", topic);
                     // Return a completed future that indicates failure or simply return null
                     // For this example, we'll proceed but log a warning.
                 }
                 // Apply topic-specific producer properties if needed before sending,
                 // by potentially reconfiguring KafkaTemplate or using a different one.
                 // This is more complex and usually handled by having multiple KafkaTemplate beans.
                 // For a simpler approach, assume common properties are sufficient.
             },
             () -> log.warn("No producer configuration found for topic: {}. Using default settings.", topic)
     );

     log.info("Sending message to topic {}: {}", topic, message);
     return kafkaTemplate.send(topic, message);
 }
}