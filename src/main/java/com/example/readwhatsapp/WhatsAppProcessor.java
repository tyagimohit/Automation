package com.example.readwhatsapp;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

public class WhatsAppProcessor {

    public static void process(Map<String, Object> payload) {

        System.out.println("---process--"+payload);

        Map entry = ((List<Map>) payload.get("entry")).get(0);
        Map change = ((List<Map>) entry.get("changes")).get(0);
        Map value = (Map) change.get("value");

        List<Map> messages = (List<Map>) value.get("messages");
        if (messages == null) return;

        Map message = messages.get(0);
        String type = (String) message.get("type");

        switch (type) {
            case "text" -> handleText(message);
        }
    }

    private static void handleText(Map message) {
        String text = (String) ((Map) message.get("text")).get("body");
        System.out.println("-----"+text);
    }


}