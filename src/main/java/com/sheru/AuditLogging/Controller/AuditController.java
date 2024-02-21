package com.sheru.AuditLogging.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sheru.AuditLogging.Model.AuditModel;
import com.sheru.AuditLogging.Model.AuditSearchModel;
import com.sheru.AuditLogging.Service.AuditService;
import com.sheru.AuditLogging.Utils.LogLevels;
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
        if (auditModel != null) {
            auditService.logAudit(LogLevels.CR, auditModel);
        } else
            return ResponseEntity.badRequest().body("Invalid action passed");
        return ResponseEntity.ok("All ok");
    }

    @GetMapping("/read-logs")
    public ResponseEntity<?> readLogs() {
        List<AuditModel> logEntries;
        try {
            logEntries = auditService.readLog("other.log");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error reading logs");
        }

        return ResponseEntity.ok(logEntries);
    }

//    @GetMapping("/find-in-logs")
//    public ResponseEntity<?> findInLogs(@RequestBody AuditSearchModel auditSearchModel) {
//        List<AuditModel> logEntries;
//        try {
//            logEntries = auditService.findInLogs(auditSearchModel.getFeature(), auditSearchModel.getFeatureId());
//        } catch (Exception e) {
//            return ResponseEntity.internalServerError().body("Error reading logs");
//        }
//        return ResponseEntity.ok(logEntries);
//    }

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
