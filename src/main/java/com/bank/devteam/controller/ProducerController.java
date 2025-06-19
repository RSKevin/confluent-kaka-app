package com.bank.devteam.controller;

import com.bank.devteam.service.KafkaProducerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.support.SendResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;

@RestController
@RequiredArgsConstructor
public class ProducerController {

 private final KafkaProducerService kafkaProducerService;

 @PostMapping("/produce")
 public ResponseEntity<String> sendMessage(@RequestParam String topic, @RequestBody String message) {
     CompletableFuture<SendResult<String, String>> future = kafkaProducerService.sendMessage(topic, message);

     future.whenComplete((result, ex) -> {
         if (ex == null) {
             System.out.println("Sent message=[" + message + "] with offset=[" + result.getRecordMetadata().offset() + "]");
         } else {
             System.err.println("Unable to send message=[" + message + "] due to : " + ex.getMessage());
         }
     });
     return ResponseEntity.ok("Message sent to Kafka. Check logs for details.");
 }
}