package com.example.ainotify.readwhatsapp.controller;

import com.example.ainotify.readwhatsapp.service.WhatsAppReadMessageService;
import com.example.ainotify.readwhatsapp.entity.ProcessedMessage;
import com.example.ainotify.readwhatsapp.entity.ProcessedMessageRepository;
import com.example.ainotify.readwhatsapp.service.InvoiceDraftService;
import com.example.ainotify.readwhatsapp.util.InvoiceOCR;
import com.example.ainotify.readwhatsapp.util.WhatsAppParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/webhook/whatsapp")
//run command : "ngrok http 8080" to start reading the received messages on test whatsapp number +1 (555) 192-0540
public class WhatsAppReadMessageController {

    private final ProcessedMessageRepository processedMessageRepo;

    @Autowired
    InvoiceOCR invoiceOCR;

    @Autowired
    WhatsAppReadMessageService whatsAppReadMessageService;

    private final InvoiceDraftService service;

    public WhatsAppReadMessageController(ProcessedMessageRepository processedMessageRepo, InvoiceDraftService service) {
        this.processedMessageRepo = processedMessageRepo;
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Void> receive(@RequestBody Map<String, Object> payload) throws Exception {

        if (isStatusEvent(payload)) {
            return ResponseEntity.ok().build();
        }

        String messageId = extractMessageId(payload);
        if (messageId == null) {
            return ResponseEntity.ok().build();
        }

        if (processedMessageRepo.existsById(messageId)) {
            return ResponseEntity.ok().build();
        }

        processedMessageRepo.save(new ProcessedMessage(messageId));

        WhatsAppParser.Event event = WhatsAppParser.parse(payload);

        String imageFile =whatsAppReadMessageService.process(payload);

        //String imageText = invoiceOCR.getTextFromImage(imagepath);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.ocr.space/parse/image"))
                .header("apikey", "helloworld")
                .POST(HttpRequest.BodyPublishers.ofFile(Path.of(imageFile)))
                .build();

        HttpClient client = HttpClient.newHttpClient();

        HttpResponse<String> response =
                client.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println("Status Code: " + response.statusCode());
        System.out.println("Response Body: " + response.body());




        if ("IMAGE".equals(event.type())) {
            service.createDraft(event.phone());
        }

        if ("BUTTON".equals(event.type())) {
            service.handleButton(event.buttonId());
        }

        if ("TEXT".equals(event.type())) {
            service.handleEdit(event.phone(), event.text());
        }


        //whatsAppReadMessageService.process(payload);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<String> verify(
            @RequestParam("hub.mode") String mode,
            @RequestParam("hub.challenge") String challenge,
            @RequestParam("hub.verify_token") String token
    ) {
        if ("subscribe".equals(mode) && "my_verify_token".equals(token)) {
            return ResponseEntity.ok(challenge);
        }
        return ResponseEntity.status(403).body("Verification failed");
    }

    private boolean isStatusEvent(Map<String, Object> payload) {
        return payload.toString().contains("\"statuses\"");
    }

    @SuppressWarnings("unchecked")
    private String extractMessageId(Map<String, Object> payload) {
        try {
            List<Map<String, Object>> entry =
                    (List<Map<String, Object>>) payload.get("entry");

            Map<String, Object> entry0 = entry.get(0);

            List<Map<String, Object>> changes =
                    (List<Map<String, Object>>) entry0.get("changes");

            Map<String, Object> change0 = changes.get(0);

            Map<String, Object> value =
                    (Map<String, Object>) change0.get("value");

            List<Map<String, Object>> messages =
                    (List<Map<String, Object>>) value.get("messages");

            return messages.get(0).get("id").toString();

        } catch (Exception e) {
            return e.getMessage();
        }
    }

    private void processImageMessage(Map<String, Object> payload) {
        System.out.println("✅ Processing image message ONCE");
    }
}