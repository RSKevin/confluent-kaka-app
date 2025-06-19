package com.bank.devteam.config;

import com.bank.devteam.model.KafkaProducerConfigEntity;
import com.bank.devteam.repository.KafkaProducerConfigRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class KafkaProducerConfig {

 private final KafkaConfigLoader kafkaConfigLoader;
 private final KafkaProducerConfigRepository kafkaProducerConfigRepository;

 @Bean
 public KafkaTemplate<String, String> kafkaTemplate() {
     return new KafkaTemplate<>(producerFactory());
 }

 @Bean
 public ProducerFactory<String, String> producerFactory() {
     Map<String, Object> props = new HashMap<>(kafkaConfigLoader.getCommonKafkaProperties());
     props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
     props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

     // Add producer-specific properties from the database for default topic if needed,
     // or set sensible defaults
     Optional<KafkaProducerConfigEntity> defaultProducerConfig = kafkaProducerConfigRepository.findByTopicName("default_producer_topic"); // Or any strategy

     defaultProducerConfig.ifPresent(config -> {
         props.put(ProducerConfig.ACKS_CONFIG, config.getAcks());
         props.put(ProducerConfig.RETRIES_CONFIG, config.getRetries());
         props.put(ProducerConfig.BATCH_SIZE_CONFIG, config.getBatchSize());
         props.put(ProducerConfig.LINGER_MS_CONFIG, config.getLingerMs());
         props.put(ProducerConfig.BUFFER_MEMORY_CONFIG, config.getBufferMemory());
         if (config.getClientId() != null) {
             props.put(ProducerConfig.CLIENT_ID_CONFIG, config.getClientId());
         }
     });

     log.info("ProducerFactory properties: {}", props);
     return new DefaultKafkaProducerFactory<>(props);
 }
}