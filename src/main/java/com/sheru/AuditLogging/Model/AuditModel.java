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
    String feature_id;
    FeatureDetails feature_details;

    // todo: change the structure of this and make specific str for create and delete

    @Override
    public String toString() {
        return "{" +
                "\"who\":\"" + who + '\"' +
                ", \"when\":\"" + when + '\"' +
                ", \"action\":\"" + action + '\"' +
                ", \"feature\":\"" + feature + '\"' +
                "\"feature_id\":\"" + feature_id + '\"' +
                ", \"feature_details\":" + feature_details +
                '}';
    }
}
