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
    String feature;
    FeatureDetails feature_details;


    @Override
    public String toString() {
        return "{" +
                "\"who\":\"" + who + '\"' +
                ", \"when\":\"" + when + '\"' +
                ", \"action\":\"" + action + '\"' +
                ", \"feature\":\"" + feature + '\"' +
                ", \"feature_details\":" + feature_details +
                '}';
    }
}
