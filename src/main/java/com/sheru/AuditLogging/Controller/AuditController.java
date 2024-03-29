package com.sheru.AuditLogging.Controller;

import com.sheru.AuditLogging.Model.AuditModel;
import com.sheru.AuditLogging.Model.AuditSearchModel;
import com.sheru.AuditLogging.Service.AuditService;
import com.sheru.AuditLogging.Service.KafkaProducerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/audit")
public class AuditController {

    @Autowired
    private AuditService auditService;

    @Autowired
    private KafkaProducerService kafkaProducerService;

    @PostMapping("/sendData")
    public ResponseEntity<String> sendData(@RequestBody String jsonData) {
        ResponseEntity<String> response = kafkaProducerService.produceMessage(jsonData);
        return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
    }

    @GetMapping("/find-logs")
    public ResponseEntity<?> findLogs(@RequestBody AuditSearchModel auditSearchModel) {
        List<AuditModel> logEntries;
        try {
            logEntries = auditService.findLogs(auditSearchModel.getFeatureId(), auditSearchModel.getPageSize(), auditSearchModel.getPageNumber());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error reading logs");
        }
        return ResponseEntity.ok(logEntries);
    }
}
