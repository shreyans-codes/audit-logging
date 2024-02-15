package com.sheru.AuditLogging.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sheru.AuditLogging.Controller.AuditController;
import com.sheru.AuditLogging.Model.AuditModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class AuditService {
    private static final Logger logger = LoggerFactory.getLogger(AuditController.class);

    public void updateAudit(AuditModel auditModel) {
        String audit = String.format("%s (%s) updated from %s to %s by %s at %s", auditModel.getTarget(), auditModel.getTarget_details(), auditModel.getPrev_state().getStatus(), auditModel.getNew_state().getStatus(), auditModel.getWho(), auditModel.getWhen());
        logger.info(auditModel.toString());
    }

    public void deleteAudit(AuditModel auditModel) {
        String audit = String.format("%s (%s) deleted by %s at %s (last state: %s)", auditModel.getTarget(), auditModel.getTarget_details(), auditModel.getWho(), auditModel.getWhen(), auditModel.getPrev_state().getStatus());
        logger.info(auditModel.toString());
    }

    public void createAudit(AuditModel auditModel) {
        String audit = String.format("%s (%s) created by %s at %s (new state: %s)", auditModel.getTarget(), auditModel.getTarget_details(), auditModel.getWho(), auditModel.getWhen(), auditModel.getNew_state().getStatus());
        logger.info(auditModel.toString());
    }

//    public void auditInJSON(AuditModel auditModel) {
//        ObjectMapper objectMapper = new ObjectMapper();
//        try {
//            String json = objectMapper.writeValueAsString(auditModel);
//            logger.info(json);
//        } catch (JsonProcessingException e) {
//            logger.error("Error converting AuditModel to JSON", e);
//        }
//    }

}
