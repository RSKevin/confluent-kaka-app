package com.bank.devteam.repository;

import com.bank.devteam.model.KafkaConsumerConfigEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface KafkaConsumerConfigRepository extends JpaRepository<KafkaConsumerConfigEntity, Long> {
 List<KafkaConsumerConfigEntity> findByEnabledTrue();
}