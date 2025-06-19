package com.bank.devteam.model;

import java.time.OffsetDateTime;

import com.fasterxml.jackson.databind.JsonNode;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "cders_crd_consumer_data")
@Data
public class CdersCrdConsumerData {
 @Id
 @GeneratedValue(strategy = GenerationType.IDENTITY)
 private Long msgId;

 @Column(name = "json_data", columnDefinition = "jsonb")
 private JsonNode jsonData;

 @Column(name = "created_ts")
 private OffsetDateTime createdTs;

 @Column(name = "updated_ts")
 private OffsetDateTime updatedTs;

 @PrePersist
 protected void onCreate() {
     createdTs = OffsetDateTime.now();
     updatedTs = OffsetDateTime.now();
 }

 @PreUpdate
 protected void onUpdate() {
     updatedTs = OffsetDateTime.now();
 }
}