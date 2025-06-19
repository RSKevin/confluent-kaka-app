package com.bank.devteam.config;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.bank.devteam.repository.KafkaConsumerConfigRepository;
import com.bank.devteam.service.KafkaConsumerService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Configuration
@EnableScheduling // Enable scheduling for potential dynamic consumer refresh
@RequiredArgsConstructor
@Slf4j
public class KafkaConsumerConfig {

 private final KafkaConfigLoader kafkaConfigLoader;
 private final KafkaConsumerConfigRepository kafkaConsumerConfigRepository;
 private final Map<String, KafkaMessageListenerContainer<String, String>> runningContainers = new ConcurrentHashMap<>();

 @Bean
 public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory() {
     ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
     factory.setConsumerFactory(consumerFactory());
     factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.RECORD); // Or BATCH, MANUAL
     return factory;
 }

 @Bean
 public ConsumerFactory<String, String> consumerFactory() {
     Map<String, Object> props = new HashMap<>(kafkaConfigLoader.getCommonKafkaProperties());
     props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
     props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
     // Common consumer properties
     props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
     props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false"); // Important for manual acknowledgement

     log.info("ConsumerFactory properties: {}", props);
     return new DefaultKafkaConsumerFactory<>(props);
 }

 @Bean
 public ApplicationReadyEventConsumerStarter applicationReadyEventConsumerStarter(
         ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory,
         KafkaConsumerService kafkaConsumerService) {
     return new ApplicationReadyEventConsumerStarter(kafkaListenerContainerFactory, kafkaConsumerService,
             kafkaConsumerConfigRepository, runningContainers);
 }
}