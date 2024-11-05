package com.example.demo.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Message {
    private String messageId;
    private String text;
}
