package com.sheru.AuditLogging.Model;

import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FeatureDetails {

    String field_name;
    FeatureChanges feature_changes;

    @Override
    public String toString() {
        return "{" +
                ", \"field_name\":\"" + field_name + '\"' +
                ", \"feature_changes\":" + feature_changes +
                '}';
    }
}
