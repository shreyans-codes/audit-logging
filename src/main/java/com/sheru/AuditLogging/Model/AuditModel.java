package com.sheru.AuditLogging.Model;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Builder
@Getter
@Setter
public class AuditModel {
    String who;
    String when;
    String action;
    String object;
    String object_details;
    String prev_state;
    String new_state;
}
