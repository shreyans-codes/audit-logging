package com.sheru.AuditLogging.Model;

import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CRState {
    Boolean status;
    Boolean implementationStatus;
    String title;
    String description;

    @Override
    public String toString() {
        return "{" +
                "\"status\":" + status +
                ", \"implementationStatus\":" + implementationStatus +
                ", \"title\":\"" + title+ '\"' +
                ", \"description\":\"" + description + '\"' +
                '}';
    }
}
