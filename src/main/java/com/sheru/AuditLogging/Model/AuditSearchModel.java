package com.sheru.AuditLogging.Model;

import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuditSearchModel {
    String featureId;
    private int pageSize;
    private int pageNumber;
}
