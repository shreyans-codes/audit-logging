package com.sheru.AuditLogging.Controller;

import com.sheru.AuditLogging.Model.AuditModel;
import com.sheru.AuditLogging.Model.AuditSearchModel;
import com.sheru.AuditLogging.Service.AuditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/audit")
public class AuditController {

    @Autowired
    private AuditService auditService;

    // Todo: no need for read-logs end point
    @GetMapping("/read-logs")
    public ResponseEntity<?> readLogs() {
        List<AuditModel> logEntries;
        try {
            logEntries = auditService.readLog("CR0001.log");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error reading logs");
        }

        return ResponseEntity.ok(logEntries);
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
