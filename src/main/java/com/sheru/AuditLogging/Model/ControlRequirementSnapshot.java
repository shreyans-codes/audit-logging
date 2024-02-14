package com.sheru.AuditLogging.Model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class ControlRequirementSnapshot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;
    @Column
    String question;
    @Column
    Boolean compliant;
    @Column
    String remarks;
}
