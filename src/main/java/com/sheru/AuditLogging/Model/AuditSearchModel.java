package com.sheru.AuditLogging.Model;

import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuditSearchModel {
    String feature;
    String featureId;
}
