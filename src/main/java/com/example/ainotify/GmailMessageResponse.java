package com.example.ainotify;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GmailMessageResponse {

    String subject;
    String from;
    String body;
    String date;

}
