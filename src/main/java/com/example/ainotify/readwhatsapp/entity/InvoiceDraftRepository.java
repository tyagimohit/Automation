package com.example.ainotify.readwhatsapp.entity;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface InvoiceDraftRepository extends JpaRepository<InvoiceDraft, UUID> {

    Optional<InvoiceDraft> findTopByPhoneAndStatusOrderByCreatedAtDesc(
            String phone, String status
    );
}
