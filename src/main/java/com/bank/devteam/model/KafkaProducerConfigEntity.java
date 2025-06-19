package com.bank.devteam.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "kafka_producer_config")
@Data
public class KafkaProducerConfigEntity {
 @Id
 @GeneratedValue(strategy = GenerationType.IDENTITY)
 private Long id;
 private String topicName;
 private String clientId;
 private String acks;
 private Integer retries;
 private Integer batchSize;
 private Integer lingerMs;
 private Long bufferMemory;
 private boolean enabled;
}