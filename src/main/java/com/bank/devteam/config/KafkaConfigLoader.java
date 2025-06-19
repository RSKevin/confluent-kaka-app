package com.bank.devteam.config;

import com.bank.devteam.repository.KafkaCommonPropertiesRepository;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaConfigLoader {

 private final KafkaCommonPropertiesRepository commonPropertiesRepository;

 @Getter
 private Map<String, Object> commonKafkaProperties;

 @PostConstruct
 public void loadKafkaCommonProperties() {
     log.info("Loading common Kafka properties from database...");
     commonKafkaProperties = new HashMap<>();
     commonPropertiesRepository.findAll().forEach(prop -> {
         commonKafkaProperties.put(prop.getPropertyKey(), prop.getPropertyValue());
         log.debug("Loaded Kafka common property: {} = {}", prop.getPropertyKey(), prop.getPropertyValue());
     });
     log.info("Finished loading common Kafka properties. Count: {}", commonKafkaProperties.size());
 }
}
