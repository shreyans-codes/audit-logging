package com.sheru.AuditLogging.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sheru.AuditLogging.Controller.AuditController;
import com.sheru.AuditLogging.Model.AuditModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
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
    private static final Logger logger = LoggerFactory.getLogger(AuditController.class);

    public void updateAudit(AuditModel auditModel) {
//        String audit = String.format("%s (%s) updated from %s to %s by %s at %s", auditModel.getTarget(), auditModel.getTarget_details(), auditModel.getPrev_state().getStatus(), auditModel.getNew_state().getStatus(), auditModel.getWho(), auditModel.getWhen());
        logger.info(auditModel.toString());
    }

    public void deleteAudit(AuditModel auditModel) {
//        String audit = String.format("%s (%s) deleted by %s at %s (last state: %s)", auditModel.getTarget(), auditModel.getTarget_details(), auditModel.getWho(), auditModel.getWhen(), auditModel.getPrev_state().getStatus());
        logger.info(auditModel.toString());
    }

    public void createAudit(AuditModel auditModel) {
//        String audit = String.format("%s (%s) created by %s at %s (new state: %s)", auditModel.getTarget(), auditModel.getTarget_details(), auditModel.getWho(), auditModel.getWhen(), auditModel.getNew_state().getStatus());
        logger.info(auditModel.toString());
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
            e.printStackTrace();
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
                    searchResults.addAll(logsWithinFile.stream().filter(entry->target.equals(entry.getTarget()) && targetId.equals(entry.getTarget_details().getId())).toList());

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
