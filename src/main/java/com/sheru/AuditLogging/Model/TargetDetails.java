package com.sheru.AuditLogging.Model;

import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TargetDetails {
    String id;
    String name;
    Integer frequency;

    @Override
    public String toString() {
        return "{" +
                "\"id\":\"" + id + '\"' +
                ", \"name\":\"" + name + '\"' +
                ", \"frequency\":" + frequency +
                '}';
    }
}
