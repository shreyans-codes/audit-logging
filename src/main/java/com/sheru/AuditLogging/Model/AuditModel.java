package com.sheru.AuditLogging.Model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

enum ACTIONS {
    CREATE,
    UPDATE,
    DELETE
}

@Builder
@Getter
@Setter
@NoArgsConstructor
public class AuditModel {

    String who;
    String when;
    String action;
    String feature;
    String feature_id;
    List<FeatureDetails> feature_details;

    @JsonCreator
    public AuditModel(@JsonProperty("who") String who, @JsonProperty("when") String when, @JsonProperty("action") String action, @JsonProperty("feature")String feature, @JsonProperty("feature_id") String feature_id, @JsonProperty("feature_details")List<FeatureDetails> feature_details) {
        this.who = who;
        this.action = action;
        this.when = when;
        this.feature = feature;
        this.feature_id = feature_id;
        this.feature_details = feature_details;
    }

    // todo: change the structure of this and make specific str for create and delete

    @Override
    public String toString() {

        StringBuilder featuresBuilder = null;
        int featuresLength = feature_details.size();
        for(int i=0;i<featuresLength;i++)
        {
            if(featuresBuilder==null)
            {
                featuresBuilder = new StringBuilder("[");
            }
            if(i==featuresLength-1)
            {
                featuresBuilder.append(feature_details.toArray()[i].toString()).append("]");
                break;
            }
            featuresBuilder.append(feature_details.toArray()[i].toString()).append(",");

        }

        return "{" +
                "\"who\":\"" + who + '\"' +
                ", \"when\":\"" + when + '\"' +
                ", \"action\":\"" + action + '\"' +
                ", \"feature\":\"" + feature + '\"' +
                ", \"feature_id\":\"" + feature_id + '\"' +
                ", \"feature_details\": " + featuresBuilder +
                '}';
    }
}
