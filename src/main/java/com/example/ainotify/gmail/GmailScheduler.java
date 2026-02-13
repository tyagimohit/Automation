package com.example.ainotify.gmail;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GmailScheduler {

    @Autowired
    GmailService gmailService;

    @Scheduled(fixedDelay = 60000) // every 1 minute
    public void checkEmails() throws Exception {
        List<GmailMessageResponse> gmailMessageList = gmailService.getUnreadMessages();
        System.out.println("gmailMessage count: " + gmailMessageList.size());
    }
}