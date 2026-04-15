package com.example.ainotify.gmail;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GmailMessageRepository extends JpaRepository<GmailMessage, Long> {

    Optional<GmailMessage> findByMessageId(String messageId);

    // Fetch all messages
    List<GmailMessage> findAll();

    // Fetch by sender
    List<GmailMessage> findByFromEmail(String fromEmail);

    // Fetch by subject keyword
    List<GmailMessage> findBySubjectContaining(String keyword);

    // Fetch latest messages
    List<GmailMessage> findTop10ByOrderByReceivedTimeDesc();
}