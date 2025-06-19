package com.bank.devteam.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostgresProcessorService {

 private final JdbcTemplate jdbcTemplate;

 @Transactional(propagation = Propagation.MANDATORY) // Ensures it runs within an existing transaction
 // @Async // Optional: Uncomment to make this method asynchronous
 public void callPostgresFunction(String functionName, Long msgId) {
     log.info("Calling PostgreSQL function: {} with msg_id: {}", functionName, msgId);
     try {
         // Use CallableStatement for stored procedures/functions with parameters
         jdbcTemplate.update("SELECT " + functionName + "(?)", msgId);
         log.info("PostgreSQL function {} called successfully for msg_id: {}", functionName, msgId);
     } catch (Exception e) {
         log.error("Error calling PostgreSQL function {} for msg_id {}: {}", functionName, msgId, e.getMessage(), e);
         // Re-throw to allow transaction rollback
         throw new RuntimeException("Failed to call PostgreSQL function", e);
     }
 }
}