package com.sheru.AuditLogging.Service;

import com.sheru.AuditLogging.Controller.AuditController;
import com.sheru.AuditLogging.Model.AuditModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class AuditService {
    private static final Logger logger = LoggerFactory.getLogger(AuditController.class);

    public void updateAudit(AuditModel auditModel) {
        String audit = String.format("%s (%s) updated from %s to %s by %s at %s", auditModel.getObject(), auditModel.getObject_details(), auditModel.getPrev_state(), auditModel.getNew_state(), auditModel.getWho(), auditModel.getWhen());
        logger.info(audit);
    }

    public void deleteAudit(AuditModel auditModel) {
        String audit = String.format("%s (%s) deleted by %s at %s (last state: %s)", auditModel.getObject(), auditModel.getObject_details(), auditModel.getWho(), auditModel.getWhen(), auditModel.getPrev_state());
        logger.info(audit);
    }

    public void createAudit(AuditModel auditModel) {
        String audit = String.format("%s (%s) created by %s at %s (new state: %s)", auditModel.getObject(), auditModel.getObject_details(), auditModel.getWho(), auditModel.getWhen(), auditModel.getNew_state());
        logger.info(audit);
        System.out.println("test");
    }


}
