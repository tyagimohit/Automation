package com.example.ainotify.gmail;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class GmailMessageResponse {

    private String id;
    private String subject;
    private String from;
    private String body;
    private String date;
    private LocalDateTime receivedTime;

}
