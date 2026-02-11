package com.example.ainotify.readwhatsapp.entity;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class WhatsAppApiClient {

    private final RestTemplate restTemplate;

    @Value("${whatsapp.phone-id}")
    private String phoneNumberId;

    @Value("${whatsapp.token}")
    private String token;

    public WhatsAppApiClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public byte[] downloadMedia(String mediaId) {

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

        // Step 1: Get media URL
        ResponseEntity<MediaResponse> mediaInfo =
                restTemplate.exchange(
                        "https://graph.facebook.com/v22.0/" + mediaId,
                        HttpMethod.GET,
                        new HttpEntity<>(headers),
                        MediaResponse.class
                );

        String mediaUrl = mediaInfo.getBody().getUrl().toString();

        // Step 2: Download media bytes
        ResponseEntity<byte[]> mediaBytes =
                restTemplate.exchange(
                        mediaUrl,
                        HttpMethod.GET,
                        new HttpEntity<>(headers),
                        byte[].class
                );

        return mediaBytes.getBody();
    }

    public void sendTextMessage(String to, String text) {

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> body = Map.of(
                "messaging_product", "whatsapp",
                "to", to,
                "type", "text",
                "text", Map.of("body", text)
        );

        restTemplate.postForEntity(
                "https://graph.facebook.com/v19.0/" + phoneNumberId + "/messages",
                new HttpEntity<>(body, headers),
                String.class
        );
    }

    public void markAsRead(String messageId) {

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> body = Map.of(
                "messaging_product", "whatsapp",
                "status", "read",
                "message_id", messageId
        );

        restTemplate.postForEntity(
                "https://graph.facebook.com/v19.0/" + phoneNumberId + "/messages",
                new HttpEntity<>(body, headers),
                String.class
        );
    }
}