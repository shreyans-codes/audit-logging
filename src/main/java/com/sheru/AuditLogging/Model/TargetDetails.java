package com.sheru.AuditLogging.Model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
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
