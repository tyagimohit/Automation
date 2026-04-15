package com.example.ainotify.gmail;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GmailDbService {

    @Autowired
    private GmailMessageRepository repository;

    public void saveMessage(GmailMessageResponse response) {

        // Avoid duplicate insert
        if (repository.findByMessageId(response.getId()).isPresent()) {
            return;
        }

        GmailMessage msg = new GmailMessage();
        msg.setMessageId(response.getId());
        msg.setSubject(response.getSubject());
        msg.setFromEmail(response.getFrom());
        msg.setBody(response.getBody());
        msg.setReceivedTime(response.getReceivedTime());

        repository.save(msg);
    }
}
