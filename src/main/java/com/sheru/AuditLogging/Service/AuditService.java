package com.sheru.AuditLogging.Service;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.rolling.*;
import ch.qos.logback.core.util.FileSize;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sheru.AuditLogging.Model.AuditModel;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.io.RandomAccessFile;
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

    public Boolean validateCRAudit(AuditModel auditModel) {
        if(auditModel.getAction().toLowerCase().trim().equals("create"))
        {
            logger.error("Create operation shouldn't have feature details");
            //Create and delete operations must not have any feature details
            //Returns true if feature_details is empty
            if(auditModel.getFeature_details() == null)
                return true;
            return auditModel.getFeature_details().isEmpty();
        } else if(auditModel.getAction().toLowerCase().trim().equals("update"))
        {
            logger.error("Update operations should have feature details");
            //Return true of feature_details is not empty
            return !auditModel.getFeature_details().isEmpty();
        } else
            // not a known action type
            return false;
    }
    @KafkaListener(topics = "${spring.kafka.consumer.topic-cr}", groupId = "${spring.kafka.consumer.group-id}")
    public void readCRAuditMessage(String message) {
            AuditModel auditModel = deserializeMessage(message);
            if (auditModel != null && auditModel.getFeature().equals("Control_Requirement")) {
                if(!validateCRAudit(auditModel))
                {
                    return;
                }

                String id = auditModel.getFeature_id();
                String loggerName = "dynamicLogger." + id;

                LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
                Logger dynamicLogger = loggerContext.getLogger(loggerName);

                // Configuration for dynamic logger
                RollingFileAppender<ILoggingEvent> fileAppender = new RollingFileAppender<>();
                fileAppender.setContext(loggerContext);
                fileAppender.setFile("logs/" + id + ".log");

                // encoder
                PatternLayoutEncoder encoder = new PatternLayoutEncoder();
                encoder.setPattern("[%msg]%n");
                encoder.setContext(loggerContext);
                encoder.start();

                fileAppender.setEncoder(encoder);

                // Configure rolling policy
                FixedWindowRollingPolicy rollingPolicy = new FixedWindowRollingPolicy();
                rollingPolicy.setFileNamePattern("logs/" + id + ".%i.log"); // Set naming pattern for rolled-over files
                rollingPolicy.setMinIndex(1);
                rollingPolicy.setMaxIndex(10);
                rollingPolicy.setParent(fileAppender); // Associate rolling policy with appender
                rollingPolicy.setContext(loggerContext);
                rollingPolicy.start();
                fileAppender.setRollingPolicy(rollingPolicy);


                // Set size-based triggering policy
                SizeBasedTriggeringPolicy<ILoggingEvent> triggeringPolicy = new SizeBasedTriggeringPolicy<>();
                triggeringPolicy.setMaxFileSize(new FileSize(10 * 1024 * 1024)); // Set maximum file size to 10 megabytes
                triggeringPolicy.setContext(loggerContext);
                triggeringPolicy.start();
                fileAppender.setTriggeringPolicy(triggeringPolicy);

                fileAppender.start();

                // Add appender to logger and set level
                dynamicLogger.addAppender(fileAppender);
                dynamicLogger.setLevel(Level.INFO);

                // Log the audit model
                dynamicLogger.info(auditModel.toString());

                // Remove the appender to avoid resource leaks
                dynamicLogger.detachAppender(fileAppender);
            }
    }


    private AuditModel deserializeMessage(String message) {
        try {
            return objectMapper.readValue(message, AuditModel.class);
        } catch (JsonProcessingException e) {
            logger.error("Error deserializing messages: {" + e.getMessage() + "}");
            return null;
        }
    }

    public List<AuditModel> findLogs(String featureId, int pageSize, int pageNumber) {
        String directoryPath = "logs/";
        List<AuditModel> searchResults = new ArrayList<>();
        try (Stream<Path> paths = Files.walk(Paths.get(directoryPath))) {
            searchResults = paths
                    .filter(Files::isRegularFile)
                    .flatMap(file -> {
                        try {
                            return readLog(file.getFileName().toString())
                                    .stream()
                                    .skip((long) (pageNumber - 1) * pageSize)
                                    .limit(pageSize);
                        } catch (Exception e) {
                            logger.error("Internal file reading error", e);
                            return Stream.empty();
                        }
                    })
                    .filter(entry -> featureId.equals(entry.getFeature_id()))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Directory access error", e);
        }
        return searchResults;
    }

    public List<AuditModel> readLog(String fileName) {
        String directoryPath = "logs/";
        String logFilePath = directoryPath + fileName;
        List<AuditModel> logEntries = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();

        try (RandomAccessFile file = new RandomAccessFile(logFilePath, "r")) {
            long filePointer = file.length() - 1;
            StringBuilder builder = new StringBuilder();

            // Read the file from the end
            while (filePointer >= 0) {
                file.seek(filePointer);
                char ch = (char) file.readByte();
                if (ch == '\n') {
                    // When a new line is found, process the accumulated line (if it's not empty)
                    if (builder.length() > 0) {
                        String logLine = builder.reverse().toString();
                        String json = logLine.substring(1, logLine.length() - 1);
                        try {
                            AuditModel logEntry = objectMapper.readValue(json, AuditModel.class);
                            logEntries.add(logEntry);
                        } catch (Exception e) {
                            logger.error("Error parsing JSON: " + e.getMessage());
                        }
                        builder = new StringBuilder(); // Reset for the next line
                    }
                } else {
                    builder.append(ch);
                }
                filePointer--;
            }
            // Process the last line if exists
            if (builder.length() > 0) {
                String logLine = builder.reverse().toString();
                String json = logLine.substring(1, logLine.length() - 1); // Adjust based on your log format
                try {
                    AuditModel logEntry = objectMapper.readValue(json, AuditModel.class);
                    logEntries.add(logEntry);
                } catch (Exception e) {
                    logger.error("Error parsing JSON: " + e.getMessage());
                }
            }
        } catch (Exception e) {
            logger.error("Error reading logs: " + e.getMessage());
        }

        // The list is reversed because we read the file from the end
        return logEntries;
    }

}
