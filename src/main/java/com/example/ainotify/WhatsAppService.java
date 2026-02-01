package com.example.ainotify;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class WhatsAppService {

    @Value("${whatsapp.token}")
    private String token;

    @Value("${whatsapp.phone-id}")
    private String phoneId;

    String baseUrl = "https://graph.facebook.com/v22.0";

    private final RestTemplate restTemplate = new RestTemplate();

    public void sendReminder(String to, String message) {

        String url = "https://graph.facebook.com/v22.0/" + phoneId + "/messages";

        String payload = String.format(
                "{"
                        + "\"messaging_product\":\"whatsapp\","
                        + "\"to\":\"%s\","
                        + "\"type\":\"template\","
                        + "\"template\":{"
                        +     "\"name\":\"festival_offers\","
                        +     "\"language\":{"
                        +         "\"code\":\"en\""
                        +     "}"
                        + "}"
                        + "}",
                to
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token.trim());

        System.out.println("Authorization header = Bearer " + token.trim());

        Map<String, Object> body = new HashMap<>();
        body.put("messaging_product", "whatsapp");
        body.put("to", to);
        body.put("type", "template");

        Map<String, Object> template = new HashMap<>();
        template.put("name", "festival_offers");

        Map<String, String> language = new HashMap<>();
        language.put("code", "en_US");

        template.put("language", language);
        body.put("template", template);

        System.out.println(body);

//        HttpEntity<Map<String, Object>> request =
//                new HttpEntity<>(body, headers);


        HttpEntity<String> request = new HttpEntity<>(payload, headers);
//        HttpEntity<Map<String, Object>> request =
//                new HttpEntity<>(body, headers);

        //restTemplate.exchange(url, HttpMethod.POST, request, String.class);

        ResponseEntity<String> response =
                restTemplate.postForEntity(url, request, String.class);

        System.out.println("📩 WhatsApp API response: " + response.getBody());
        System.out.println("📩 Status: " + response.getStatusCode());
    }

    public void sendFestivalOffer(
            String customerPhone,
            String customerName,
            String salonName,
            String offerText,
            String validTill
    ) {

        String url = baseUrl + "/" + phoneId + "/messages";

        Map<String, Object> payload = new HashMap<>();
        payload.put("messaging_product", "whatsapp");
        payload.put("to", customerPhone);
        payload.put("type", "template");

        // Template section
        Map<String, Object> template = new HashMap<>();
        template.put("name", "festival_offers");

        Map<String, String> language = new HashMap<>();
        language.put("code", "en");
        template.put("language", language);

        // Template parameters
        List<Map<String, Object>> parameters = new ArrayList<>();
        parameters.add(param(customerName));
        parameters.add(param(salonName));
        parameters.add(param(offerText));
        parameters.add(param(validTill));

        Map<String, Object> body = new HashMap<>();
        body.put("parameters", parameters);

        template.put("components", List.of(
                Map.of("type", "body", "parameters", parameters)
        ));

        payload.put("template", template);

        // Headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        HttpEntity<Map<String, Object>> entity =
                new HttpEntity<>(payload, headers);

        ResponseEntity<String> response =
                restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("WhatsApp message failed: " + response.getBody());
        }
    }

    private Map<String, Object> param(String value) {
        return Map.of(
                "type", "text",
                "text", value
        );
    }

}