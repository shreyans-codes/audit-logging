package com.sheru.AuditLogging.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerService {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    public void produceMessage(String jsonData) {
        // Send the JSON message to Kafka
        kafkaTemplate.sendDefault(jsonData);
    }
}
