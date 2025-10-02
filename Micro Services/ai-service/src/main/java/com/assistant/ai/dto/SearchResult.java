package com.assistant.ai.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SearchResult {
    public  String id;
    public  String content;
    public  double distance; // "score" from AS, lower is more similar for COSINE/IP distance
}
