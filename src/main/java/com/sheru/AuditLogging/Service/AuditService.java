package com.sheru.AuditLogging.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sheru.AuditLogging.Controller.AuditController;
import com.sheru.AuditLogging.Model.AuditModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Service
public class AuditService {

    @Autowired
    private ObjectMapper objectMapper;
    private static final Logger logger = LoggerFactory.getLogger(AuditController.class);

    public void logAudit(AuditModel auditModel) { logger.info(auditModel.toString()); }


    @KafkaListener(topics = "${spring.kafka.consumer.topic}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeMessage(String message) {
        try {
            AuditModel auditModel = deserializeMessage(message);
            if (auditModel != null) {
                logAudit(auditModel);
            }
        } catch (Exception e) {
            System.out.println("Error consuming message from Kafka: {" + e.getMessage() + "}");
        }
    }

    private AuditModel deserializeMessage(String message) {
        try {
            return  objectMapper.readValue(message, AuditModel.class);
        } catch (JsonProcessingException e) {
            System.out.println("Error deserializing messages: {" + e.getMessage() + "}");
            return null;
        }
    }

    public List<AuditModel> readLog(String fileName) {
        String logFilePath = "log/"+fileName;
        List<AuditModel> logEntries = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();
        try (BufferedReader reader = new BufferedReader(new FileReader(logFilePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String json = line.substring(1, line.length() - 1);
                AuditModel logEntry = objectMapper.readValue(json, AuditModel.class);
                logEntries.add(logEntry);
            }
        } catch (Exception e) {
            System.out.println("Error reading logs: {" + e.getMessage() + "}");
        }

        return logEntries;
    }

    public List<AuditModel> findInLogs(String target, String targetId) {
        String directoryPath = "log";
        List<AuditModel> searchResults = new ArrayList<>();
        try (Stream<Path> paths = Files.walk(Paths.get(directoryPath))) {
            paths.filter(Files::isRegularFile).forEach(file -> {
                try {
                    List<AuditModel> logsWithinFile = readLog(file.getFileName().toString());
                    searchResults.addAll(logsWithinFile.stream().filter(entry->target.equals(entry.getFeature()) && targetId.equals(entry.getFeature_details().getId())).collect(Collectors.toList())  );

                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return searchResults;
    }

}
