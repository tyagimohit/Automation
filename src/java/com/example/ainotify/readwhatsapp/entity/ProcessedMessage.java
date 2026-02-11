package com.example.ainotify.readwhatsapp.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "processed_message")
public class ProcessedMessage {

    @Id
    private String messageId;

    private LocalDateTime processedAt = LocalDateTime.now();

    protected ProcessedMessage() {}

    public ProcessedMessage(String messageId) {
        this.messageId = messageId;
    }

    public String getMessageId() {
        return messageId;
    }
}