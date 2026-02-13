package com.example.ainotify.readwhatsapp.entity;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class Message {
    private String from;
    private String id;
    private String timestamp;
    private String type;

    private String text;
    private Image image;
}