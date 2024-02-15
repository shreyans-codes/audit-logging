package com.sheru.AuditLogging.Model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
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
                ", \"title\":\"" + title + '\"' +
                ", \"description\":\"" + description + '\"' +
                '}';
    }
}
