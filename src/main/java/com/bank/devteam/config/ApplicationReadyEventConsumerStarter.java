package com.bank.devteam.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.stereotype.Component;

import com.bank.devteam.model.KafkaConsumerConfigEntity;
import com.bank.devteam.repository.KafkaConsumerConfigRepository;
import com.bank.devteam.service.KafkaConsumerService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class ApplicationReadyEventConsumerStarter {

 private final ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory;
 private final KafkaConsumerService kafkaConsumerService;
 private final KafkaConsumerConfigRepository kafkaConsumerConfigRepository;
 private final Map<String, KafkaMessageListenerContainer<String, String>> runningContainers;

 @EventListener(ApplicationReadyEvent.class)
 public void startDynamicConsumersOnApplicationReady() {
     log.info("Application is ready. Starting dynamic Kafka consumers...");
     List<KafkaConsumerConfigEntity> consumerConfigs = kafkaConsumerConfigRepository.findByEnabledTrue();

     for (KafkaConsumerConfigEntity config : consumerConfigs) {
         startConsumer(config);
     }
     log.info("Dynamic Kafka consumers started. Total: {}", runningContainers.size());
 }

 // You can also add a scheduled task to refresh consumers if configurations change
 // @Scheduled(fixedRate = 60000) // Every minute
 public void refreshDynamicConsumers() {
     log.info("Refreshing dynamic Kafka consumers...");
     List<KafkaConsumerConfigEntity> currentConfigs = kafkaConsumerConfigRepository.findByEnabledTrue();
     List<String> currentTopics = currentConfigs.stream()
             .map(KafkaConsumerConfigEntity::getTopicName)
             .toList();

     // Stop consumers for topics no longer enabled
     runningContainers.entrySet().removeIf(entry -> {
         String topic = entry.getKey();
         KafkaMessageListenerContainer<String, String> container = entry.getValue();
         if (!currentTopics.contains(topic)) {
             log.info("Stopping consumer for disabled topic: {}", topic);
             container.stop();
             return true; // Remove from map
         }
         return false;
     });

     // Start new consumers or restart if configuration changed (though for simple cases, restart isn't strictly needed for config changes)
     for (KafkaConsumerConfigEntity config : currentConfigs) {
         if (!runningContainers.containsKey(config.getTopicName())) {
             startConsumer(config);
         }
         // For complex scenarios, you might want to compare configs and restart if significant changes occurred
     }
     log.info("Dynamic Kafka consumers refreshed. Total: {}", runningContainers.size());
 }


 private void startConsumer(KafkaConsumerConfigEntity config) {
     try {
         log.info("Attempting to start consumer for topic: {} with group_id: {}", config.getTopicName(), config.getGroupId());

         ContainerProperties containerProps = new ContainerProperties(config.getTopicName());
         // Pass the entire config entity to the listener to make it available for processing
         containerProps.setMessageListener((MessageListener<String, String>) data ->
                 kafkaConsumerService.processMessage(data.value(), config)
         );

         // Create a new consumer factory for each unique group ID (if needed, otherwise use the shared one)
         // For simplicity, we'll reuse the main factory and rely on the group_id set in KafkaConfigLoader
         // Or you can create a new ConsumerFactory per group_id if you need distinct settings for each group
         Map<String, Object> consumerProps = new HashMap<>(kafkaListenerContainerFactory.getConsumerFactory().getConfigurationProperties());
         consumerProps.put("group.id", config.getGroupId()); // Override group.id for each consumer

         // Create a dedicated ConsumerFactory for this listener to allow unique group IDs
         DefaultKafkaConsumerFactory<String, String> individualConsumerFactory =
                 new DefaultKafkaConsumerFactory<>(consumerProps);

         KafkaMessageListenerContainer<String, String> container =
                 new KafkaMessageListenerContainer<>(individualConsumerFactory, containerProps);

         container.setBeanName("dynamicConsumer-" + config.getTopicName());
         container.start();
         runningContainers.put(config.getTopicName(), container);
         log.info("Started consumer for topic: {} with group_id: {}", config.getTopicName(), config.getGroupId());
     } catch (Exception e) {
         log.error("Failed to start consumer for topic {}: {}", config.getTopicName(), e.getMessage(), e);
     }
 }
}