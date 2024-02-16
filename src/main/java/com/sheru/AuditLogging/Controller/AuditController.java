package com.sheru.AuditLogging.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sheru.AuditLogging.Model.AuditModel;
import com.sheru.AuditLogging.Service.AuditService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping("/audit")
public class AuditController {
    @Autowired
    private AuditService auditService;
    @PostMapping("/cr-change")
    public ResponseEntity<?> performAudit(@RequestBody AuditModel auditModel) {
        if(auditModel.getAction().equals("Update"))
            auditService.updateAudit(auditModel);
        else if(auditModel.getAction().equals("Delete"))
            auditService.deleteAudit(auditModel);
        else if(auditModel.getAction().equals("Create"))
            auditService.createAudit(auditModel);
        else
            return ResponseEntity.badRequest().body("Invalid action passed");
        return ResponseEntity.ok("All ok");
    }

    @GetMapping("/read-logs")
    public ResponseEntity<?> readLogs() {
        String logFilePath = "log/logFile.log";
        List<Object> logEntries = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();
        try (BufferedReader reader = new BufferedReader(new FileReader(logFilePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String json = line.substring(1, line.length() - 1);
                Object logEntry = objectMapper.readValue(json, Object.class);
                logEntries.add(logEntry);
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error reading logs");
        }

        return ResponseEntity.ok(logEntries);

    }
}
