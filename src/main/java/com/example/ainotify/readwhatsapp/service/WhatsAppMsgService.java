package com.example.ainotify.readwhatsapp.service;

import com.example.ainotify.readwhatsapp.entity.InvoiceDraft;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class WhatsAppMsgService {

    @Value("${whatsapp.token}")
    private String token;

    @Value("${whatsapp.phone-id}")
    private String phoneNumberId;

    @Value("${whatsapp.api-url}")
    private String apiUrl;

    private final ObjectMapper mapper = new ObjectMapper();
    private final HttpClient client = HttpClient.newHttpClient();


    public void sendPreview(InvoiceDraft d) {

        if(!d.getPreviewSent()){
            System.out.println(
                    "Invoice Preview\n" +
                            "Customer: " + d.getCustomerName() + "\n" +
                            "Total: " + d.getTotalAmount() + "\n" +
                            "Buttons: APPROVE_" + d.getId() +
                            " EDIT_" + d.getId() +
                            " CANCEL_" + d.getId()
            );

            try {
                String payload = mapper.writeValueAsString(
                        buildInteractivePayload(d)
                );

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(apiUrl + "/" + phoneNumberId + "/messages"))
                        .header("Authorization", "Bearer " + token)
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(payload))
                        .build();

                HttpResponse<String> response =
                        client.send(request, HttpResponse.BodyHandlers.ofString());

                System.out.println("WhatsApp response: " + response.body());

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void sendText(String to, String text) {

        try {
            String payload = """
            {
              "messaging_product": "whatsapp",
              "to": "%s",
              "type": "text",
              "text": { "body": "%s" }
            }
            """.formatted(to, text);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl + "/" + phoneNumberId + "/messages"))
                    .header("Authorization", "Bearer " + token)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(payload))
                    .build();

            client.send(request, HttpResponse.BodyHandlers.ofString());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Map<String, Object> buildInteractivePayload(InvoiceDraft d) {

        Map<String, Object> payload = new HashMap<>();
        payload.put("messaging_product", "whatsapp");
        payload.put("to", d.getPhone());
        payload.put("type", "interactive");

        Map<String, Object> interactive = new HashMap<>();
        interactive.put("type", "button");

        Map<String, Object> body = new HashMap<>();
        body.put(
                "text",
                "🧾 Invoice Preview\n" +
                        "Customer: " + d.getCustomerName() + "\n" +
                        "Total: ₹" + d.getTotalAmount()
        );

        Map<String, Object> action = new HashMap<>();
        action.put("buttons", List.of(
                button("APPROVE_" + d.getId(), "Approve"),
                button("EDIT_" + d.getId(), "Edit"),
                button("CANCEL_" + d.getId(), "Cancel")
        ));

        interactive.put("body", body);
        interactive.put("action", action);
        payload.put("interactive", interactive);

        return payload;
    }

    private Map<String, Object> button(String id, String title) {

        Map<String, Object> reply = new HashMap<>();
        reply.put("id", id);
        reply.put("title", title);

        Map<String, Object> button = new HashMap<>();
        button.put("type", "reply");
        button.put("reply", reply);

        return button;
    }
}