package com.sheru.AuditLogging.Model;

import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FeatureDetails {
    String id;
    String field;
    FeatureChanges feature_changes;

    @Override
    public String toString() {
        return "{" +
                "\"id\":\"" + id + '\"' +
                ", \"field\":\"" + field + '\"' +
                ", \"feature_changes\":" + feature_changes +
                '}';
    }
}
