package com.sheru.AuditLogging.Model;


import lombok.*;


@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuditModel {
    String who;
    String when;
    String action;
    String target;
    TargetDetails target_details;
    CRState prev_state;
    CRState new_state;


    @Override
    public String toString() {
        return "{" +
                "\"who\":\"" + who + '\"' +
                ", \"when\":\"" + when + '\"' +
                ", \"action\":\"" + action + '\"' +
                ", \"target\":\"" + target + '\"' +
                ", \"target_details\":" + target_details +
                ", \"prev_state\":" + prev_state +
                ", \"new_state\":" + new_state +
                '}';
    }
}
