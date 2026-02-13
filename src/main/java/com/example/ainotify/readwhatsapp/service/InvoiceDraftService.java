package com.example.ainotify.readwhatsapp.service;

import com.example.ainotify.readwhatsapp.entity.InvoiceDraft;
import com.example.ainotify.readwhatsapp.entity.InvoiceDraftRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class InvoiceDraftService {

    private final InvoiceDraftRepository repo;
    private final WhatsAppMsgService whatsapp;
    private final TallyService tally;
    private final AiEditService ai;

    public InvoiceDraftService(
            InvoiceDraftRepository repo,
            WhatsAppMsgService whatsapp,
            TallyService tally,
            AiEditService ai) {

        this.repo = repo;
        this.whatsapp = whatsapp;
        this.tally = tally;
        this.ai = ai;
    }

    public void createDraft(String phone) {

        InvoiceDraft d = new InvoiceDraft();
        d.setPhone(phone);
        d.setCustomerName("Rahul Traders");
        d.setItemsJson("[{\"item\":\"Parle G\",\"qty\":20,\"rate\":10}]");
        d.setGstPercent(18.0);
        d.setTotalAmount(236.0);
        d.setStatus("DRAFT");
        d.setPreviewSent(false);
        repo.save(d);
        whatsapp.sendPreview(d);
    }

    public void handleButton(String buttonId) {

        String[] parts = buttonId.split("_");
        UUID id = UUID.fromString(parts[1]);

        InvoiceDraft draft = repo.findById(id).orElseThrow();

        if (buttonId.startsWith("APPROVE")) {
            approve(draft);
        }

        if (buttonId.startsWith("EDIT")) {
            whatsapp.sendText(draft.getPhone(), "Please type changes");
        }

        if (buttonId.startsWith("CANCEL")) {
            draft.setStatus("CANCELLED");
            repo.save(draft);
            whatsapp.sendText(draft.getPhone(), "Invoice cancelled");
        }
    }

    private void approve(InvoiceDraft draft) {

        if (!"DRAFT".equals(draft.getStatus())) return;

        draft.setStatus("APPROVED");
        repo.save(draft);

        String invoiceNo = tally.createInvoice(draft);

        draft.setStatus("POSTED");
        repo.save(draft);

        whatsapp.sendText(
                draft.getPhone(),
                "Invoice created: " + invoiceNo
        );
    }

    public void handleEdit(String phone, String text) {

        InvoiceDraft draft = repo
                .findTopByPhoneAndStatusOrderByCreatedAtDesc(phone, "DRAFT")
                .orElse(null);

        if (draft == null) return;

        InvoiceDraft updated = ai.apply(draft, text);
        repo.save(updated);
        whatsapp.sendPreview(updated);
    }
}