package com.bank.devteam.repository;

import com.bank.devteam.model.KafkaProducerConfigEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface KafkaProducerConfigRepository extends JpaRepository<KafkaProducerConfigEntity, Long> {
 Optional<KafkaProducerConfigEntity> findByTopicName(String topicName);
}