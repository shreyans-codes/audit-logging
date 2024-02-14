package com.sheru.AuditLogging.Controller;

import com.sheru.AuditLogging.Model.AuditModel;
import com.sheru.AuditLogging.Service.AuditService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


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
}
