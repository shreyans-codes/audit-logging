package com.sheru.AuditLogging.Service;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.FileAppender;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sheru.AuditLogging.Model.AuditModel;
import com.sheru.AuditLogging.Utils.LogLevels;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.slf4j.Marker;
import org.springframework.beans.factory.annotation.Autowired;
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
    private static final Logger logger = (Logger) LoggerFactory.getLogger("com.sheru.AuditLogging.Controller.AuditController");


    public void logAudit(Marker level, AuditModel auditModel) {
        System.out.println(String.format("MDC cr-feature-id set to: %s", auditModel.getFeature_details().getId()));
        MDC.put("cr-feature-id", auditModel.getFeature_details().getId());
        System.out.println(String.format("MDC cr-feature-id set to: %s", MDC.get("cr-feature-id")));
        updateLogFile(auditModel.getFeature_details().getId());
        logger.info(level, auditModel.toString());
        MDC.remove("cr-feature-id");
    }

    private void updateLogFile(String crFeatureId) {
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        FileAppender<?> fileAppender = (FileAppender<?>) logger.getAppender("CR_FILE");

        if (fileAppender != null) {
            String logFileName = "logs/CR/" + crFeatureId + ".log";
            fileAppender.setFile(logFileName);
            fileAppender.setAppend(true);
            fileAppender.start();
        } else {
            // Handle the case where the appender is not found
            System.out.println("CR_FILE appender not found!");
        }
    }



    @KafkaListener(topics = "${spring.kafka.consumer.topic-cr}", groupId = "${spring.kafka.consumer.group-id}")
    public void readCRAuditMessage(String message) {
        try {
            AuditModel auditModel = deserializeMessage(message);
            if (auditModel != null) {
                String id = auditModel.getFeature_details().getId();
                String loggerName = "dynamicLogger." + id;

                LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
                Logger dynamicLogger = loggerContext.getLogger(loggerName);

                // Configure appender for this dynamic logger
                FileAppender<ILoggingEvent> fileAppender = new FileAppender<>();
                fileAppender.setContext(loggerContext);
                fileAppender.setFile("logs/" + id + ".log");

                PatternLayoutEncoder encoder = new PatternLayoutEncoder();
                encoder.setPattern("[%msg]%n");
                encoder.setContext(loggerContext);
                encoder.start();

                fileAppender.setEncoder(encoder);
                fileAppender.start();

                dynamicLogger.addAppender(fileAppender);
                dynamicLogger.setLevel(Level.INFO);

                dynamicLogger.info(auditModel.toString());

                // Remove the appender to avoid resource leaks
                dynamicLogger.detachAppender(fileAppender);
            }
        } catch (Exception e) {
            logger.error("Error consuming message from Kafka", e);
        }
    }

    @KafkaListener(topics = "${spring.kafka.consumer.topic-task}", groupId = "${spring.kafka.consumer.group-id}")
    public void readTaskAuditMessage(String message) {
        try {
            AuditModel auditModel = deserializeMessage(message);
            if (auditModel != null) {
                logAudit(LogLevels.TASK, auditModel);
            }
        } catch (Exception e) {
            System.out.println("Error consuming message from Kafka: {" + e.getMessage() + "}");
        }
    }

    private AuditModel deserializeMessage(String message) {
        try {
            return objectMapper.readValue(message, AuditModel.class);
        } catch (JsonProcessingException e) {
            System.out.println("Error deserializing messages: {" + e.getMessage() + "}");
            return null;
        }
    }

    public List<AuditModel> readLog(String fileName) {
        String directoryPath = "logs/";
        String logFilePath = directoryPath + fileName;
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

    public List<AuditModel> findInLogs(String feature, String featureId) {
        String directoryPath = "logs";
        if (feature.equals("TASK"))
            directoryPath = "logs/TASK";
        else if (feature.equals("Control_Requirement"))
            directoryPath = "logs/CR";
        List<AuditModel> searchResults = new ArrayList<>();
        try (Stream<Path> paths = Files.walk(Paths.get(directoryPath))) {
            paths.filter(Files::isRegularFile).forEach(file -> {
                try {
                    List<AuditModel> logsWithinFile = readLog(file.getFileName().toString());
                    searchResults.addAll(logsWithinFile.stream().filter(entry -> feature.equals(entry.getFeature()) && featureId.equals(entry.getFeature_details().getId())).collect(Collectors.toList()));
                } catch (Exception e) {
                    System.out.println(String.format("Internal file reading error finding feature: {} with ID: {}", feature, featureId));
                }
            });
        } catch (Exception e) {
            System.out.println(String.format("Directory access error while finding feature: {} with ID: {}", feature, featureId));
        }
        return searchResults;
    }


    public List<AuditModel> findLogs(String featureId, int pageSize, int pageNumber) {
        String directoryPath = "logs/";


        List<AuditModel> searchResults = new ArrayList<>();
        try (Stream<Path> paths = Files.walk(Paths.get(directoryPath))) {
            searchResults = paths
                    .filter(Files::isRegularFile)
                    .skip((long) (pageNumber - 1) * pageSize) // Skip to the starting index of the current page
                    .limit(pageSize) // Limit the number of files to the page size
                    .flatMap(file -> {
                        try {
                            return readLog(file.getFileName().toString()).stream();
                        } catch (Exception e) {
                            logger.error("Internal file reading error", e);
                            return Stream.empty();
                        }
                    })
                    .filter(entry -> featureId.equals(entry.getFeature_details().getId()))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Directory access error", e);
        }
        return searchResults;
    }

}
