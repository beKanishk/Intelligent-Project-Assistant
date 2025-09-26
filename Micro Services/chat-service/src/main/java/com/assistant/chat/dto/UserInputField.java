package com.assistant.chat.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserInputField {
    @JsonProperty("field_name")
    private String fieldName;

    @JsonProperty("field_description")
    private String fieldDescription;

    @JsonProperty("field_type")
    private String fieldType; // "str", "int", "boolean", "select", etc.

    private boolean required = false;

    @JsonProperty("default_value")
    private String defaultValue;

    // For dropdown/select fields
    private List<String> options;

    // For validation
    @JsonProperty("min_length")
    private Integer minLength;

    @JsonProperty("max_length")
    private Integer maxLength;

    // For number fields
    @JsonProperty("min_value")
    private Number minValue;

    @JsonProperty("max_value")
    private Number maxValue;

    // For validation patterns (regex)
    private String pattern;

    // Help text or placeholder
    private String placeholder;
}
