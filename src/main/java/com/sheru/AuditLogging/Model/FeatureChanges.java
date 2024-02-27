package com.sheru.AuditLogging.Model;

import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FeatureChanges {
    String prev_state;
    String new_state;

    @Override
    public String toString() {
        String prev_message;
        String new_message;
        String finalMessage = null;
        if(prev_state!=null)
        {
            prev_message = "\"prev_state\":" + prev_state;
            finalMessage = "{" + prev_message;
        }
        if(new_state!=null)
        {
            new_message = "\"new_state\":" + new_state;
            if(finalMessage!=null)
            {
                finalMessage = finalMessage + ", " + new_message + "}";
            } else {
                finalMessage =  "{" + new_message + "}";
            }
        }


        return finalMessage;
    }
}
