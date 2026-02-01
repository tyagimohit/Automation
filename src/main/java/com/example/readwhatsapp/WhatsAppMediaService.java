package com.example.readwhatsapp;

import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

public class WhatsAppMediaService {

    private static final String TOKEN = "my_verify_token";
    private static final String GRAPH_URL = "https://graph.facebook.com/v24.0/";

    public static byte[] download(String mediaId) {

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(TOKEN);

        // Step 1: Get media URL
        ResponseEntity<Map> metaResponse =
                restTemplate.exchange(
                        GRAPH_URL + mediaId,
                        HttpMethod.GET,
                        new HttpEntity<>(headers),
                        Map.class
                );

        String mediaUrl = (String) metaResponse.getBody().get("url");

        // Step 2: Download file
        ResponseEntity<byte[]> mediaResponse =
                restTemplate.exchange(
                        mediaUrl,
                        HttpMethod.GET,
                        new HttpEntity<>(headers),
                        byte[].class
                );

        return mediaResponse.getBody();
    }
}