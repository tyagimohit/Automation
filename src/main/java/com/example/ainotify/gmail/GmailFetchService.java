package com.example.ainotify.gmail;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GmailFetchService {

    @Autowired
    private GmailMessageRepository repository;

    // Get all messages
    public List<GmailMessage> getAllMessages() {
        return repository.findAll();
    }

    // Get latest 10 messages
    public List<GmailMessage> getLatestMessages() {
        return repository.findTop10ByOrderByReceivedTimeDesc();
    }

    // Filter by sender
    public List<GmailMessage> getBySender(String email) {
        return repository.findByFromEmail(email);
    }

    // Search by subject
    public List<GmailMessage> searchBySubject(String keyword) {
        return repository.findBySubjectContaining(keyword);
    }
}