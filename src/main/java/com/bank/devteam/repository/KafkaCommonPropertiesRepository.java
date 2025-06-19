package com.bank.devteam.repository;

import com.bank.devteam.model.KafkaCommonPropertiesEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public interface KafkaCommonPropertiesRepository extends JpaRepository<KafkaCommonPropertiesEntity, Long> {
 List<KafkaCommonPropertiesEntity> findAll();

 default Map<String, String> findAllAsMap() {
     return findAll().stream()
             .collect(Collectors.toMap(KafkaCommonPropertiesEntity::getPropertyKey, KafkaCommonPropertiesEntity::getPropertyValue));
 }
}