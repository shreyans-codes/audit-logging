package com.sheru.AuditLogging.Service;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class KafkaProducerService {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;


    public ResponseEntity<String> produceMessage(String jsonData) {
        // Validate the input JSON data
        ValidationResult validationResult = validate(jsonData);
        if (!validationResult.isValid()) {
            // Return specific error message based on validation result
            return ResponseEntity.status(validationResult.getHttpStatus()).body(validationResult.getMessage());
        }

        // Add system date and time
        String messageWithDateTime = addSystemDateTime(jsonData);

        // Send the message to Kafka
        try {
            kafkaTemplate.sendDefault(messageWithDateTime);
            return ResponseEntity.ok("Message successfully sent to Kafka");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error while sending message to Kafka");
        }
    }

    private ValidationResult validate(String jsonData) {
        try {
            // Parse the jsonData to a JSON object
            JsonObject jsonObject = JsonParser.parseString(jsonData).getAsJsonObject();

            // Check if mandatory fields are present and not null or empty
            if (!jsonObject.has("who") || jsonObject.get("who").isJsonNull() || jsonObject.get("who").getAsString().isEmpty()) {
                return new ValidationResult(HttpStatus.BAD_REQUEST, "Field 'who' cannot be null or empty");
            }
            if (!jsonObject.has("action") || jsonObject.get("action").isJsonNull() || jsonObject.get("action").getAsString().isEmpty()) {
                return new ValidationResult(HttpStatus.BAD_REQUEST, "Field 'action' cannot be null or empty");
            }
            if (!jsonObject.has("feature") || jsonObject.get("feature").isJsonNull() || jsonObject.get("feature").getAsString().isEmpty()) {
                return new ValidationResult(HttpStatus.BAD_REQUEST, "Field 'feature' cannot be null or empty");
            }
            if (!jsonObject.has("feature_id") || jsonObject.get("feature_id").isJsonNull() || jsonObject.get("feature_id").getAsString().isEmpty()) {
                return new ValidationResult(HttpStatus.BAD_REQUEST, "Field 'feature_id' cannot be null or empty");
            }

            // Check if action is valid
            String action = jsonObject.get("action").getAsString().toLowerCase();
            if (!action.equals("create") && !action.equals("delete") && !action.equals("update")) {
                return new ValidationResult(HttpStatus.BAD_REQUEST, "Invalid value for 'action'. Allowed values are 'create', 'delete', 'update'");
            }

            // Check if feature_details present for DELETE and UPDATE actions
            if ((action.equals("delete") || action.equals("update")) && (!jsonObject.has("feature_details") || jsonObject.get("feature_details").isJsonNull())) {
                return new ValidationResult(HttpStatus.BAD_REQUEST, "Field 'feature_details' is required for DELETE and UPDATE actions");
            }

            // Check if all fields are present and not null for DELETE action
            if (action.equals("delete")) {
                JsonArray featureDetailsArray = jsonObject.getAsJsonArray("feature_details");
                if (featureDetailsArray == null || featureDetailsArray.isEmpty()) {
                    return new ValidationResult(HttpStatus.BAD_REQUEST, "Field 'feature_details' cannot be null or empty for DELETE action");
                }
                for (JsonElement element : featureDetailsArray) {
                    JsonObject featureDetail = element.getAsJsonObject();
                    if (!featureDetail.has("field_name") || featureDetail.get("field_name").isJsonNull() || featureDetail.get("field_name").getAsString().isEmpty()) {
                        return new ValidationResult(HttpStatus.BAD_REQUEST, "Field 'field_name' cannot be null or empty inside 'feature_details' for DELETE action");
                    }
                }
            }

            // Check if all fields are present and not null for UPDATE action
            if (action.equals("update")) {
                JsonArray featureDetailsArray = jsonObject.getAsJsonArray("feature_details");
                if (featureDetailsArray == null || featureDetailsArray.isEmpty()) {
                    return new ValidationResult(HttpStatus.BAD_REQUEST, "Field 'feature_details' cannot be null or empty for UPDATE action");
                }
                for (JsonElement element : featureDetailsArray) {
                    JsonObject featureDetail = element.getAsJsonObject();
                    if (!featureDetail.has("field_name") || featureDetail.get("field_name").isJsonNull() || featureDetail.get("field_name").getAsString().isEmpty()) {
                        return new ValidationResult(HttpStatus.BAD_REQUEST, "Field 'field_name' cannot be null or empty inside 'feature_details' for UPDATE action");
                    }
                    if (!featureDetail.has("feature_changes") || featureDetail.get("feature_changes").isJsonNull()) {
                        return new ValidationResult(HttpStatus.BAD_REQUEST, "Field 'feature_changes' is required inside 'feature_details' for UPDATE action");
                    }
                    JsonObject featureChanges = featureDetail.getAsJsonObject("feature_changes");
                    if (!featureChanges.has("prev_state") || featureChanges.get("prev_state").isJsonNull()) {
                        return new ValidationResult(HttpStatus.BAD_REQUEST, "Field 'prev_state' is required inside 'feature_changes' for UPDATE action");
                    }
                    if (!featureChanges.has("new_state") || featureChanges.get("new_state").isJsonNull()) {
                        return new ValidationResult(HttpStatus.BAD_REQUEST, "Field 'new_state' is required inside 'feature_changes' for UPDATE action");
                    }
                }
            }

            return new ValidationResult(HttpStatus.OK, "JSON data is valid");
        } catch (Exception e) {
            // JSON parsing or other exceptions
            return new ValidationResult(HttpStatus.BAD_REQUEST, "Invalid JSON data");
        }
    }

    @Getter
    static class ValidationResult {
        private final HttpStatus httpStatus;
        private final String message;

        public ValidationResult(HttpStatus httpStatus, String message) {
            this.httpStatus = httpStatus;
            this.message = message;
        }

        public boolean isValid() {
            return httpStatus == HttpStatus.OK;
        }
    }


    private String addSystemDateTime(String jsonData) {
        // Get the current system date and time
        LocalDateTime currentTime = LocalDateTime.now();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        String formattedDateTime = currentTime.format(formatter);

        // Parse the jsonData to a JSON object
        JsonObject jsonObject = JsonParser.parseString(jsonData).getAsJsonObject();

        // Add the "when" field with the formattedDateTime to the JSON object
        jsonObject.addProperty("when", formattedDateTime);

        // Return the modified JSON message
        return jsonObject.toString();
    }
}
