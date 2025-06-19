package com.bank.devteam.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "kafka_common_properties")
@Data
public class KafkaCommonPropertiesEntity {
 @Id
 @GeneratedValue(strategy = GenerationType.IDENTITY)
 private Long id;
 private String propertyKey;
 private String propertyValue;
}