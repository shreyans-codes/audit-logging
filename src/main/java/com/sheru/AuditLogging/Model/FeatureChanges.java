package com.sheru.AuditLogging.Model;

import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FeatureChanges {
    Boolean prev_state;
    Boolean new_state;

    @Override
    public String toString() {
        return "{" +
                "\"prev_state\":" + prev_state +
                ", \"new_state\":" + new_state +
                '}';
    }
}
