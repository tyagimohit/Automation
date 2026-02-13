package com.example.ainotify.readwhatsapp.entity;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class WhatsAppMediaEntity {

    private Long id;

    private String mediaId;
    private String fromNumber;
    private String type;
    private String caption;
    private String filePath;
    private Instant receivedAt;
}