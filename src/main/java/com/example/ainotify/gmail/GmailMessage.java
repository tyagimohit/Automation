package com.example.ainotify.gmail;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "gmail_messages")
public class GmailMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String messageId;

    private String subject;
    private String fromEmail;

    @Column(columnDefinition = "TEXT")
    private String body;

    private LocalDateTime receivedTime;


}