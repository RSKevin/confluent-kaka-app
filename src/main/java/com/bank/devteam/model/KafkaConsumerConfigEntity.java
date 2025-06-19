package com.bank.devteam.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "kafka_consumer_config")
@Data
public class KafkaConsumerConfigEntity {
 @Id
 @GeneratedValue(strategy = GenerationType.IDENTITY)
 private Long id;
 private String topicName;
 private String groupId;
 private String tableName;
 private String functionName;
 private boolean enabled;
}
