package com.example.ainotify.readwhatsapp.util;

import java.util.List;
import java.util.Map;

public class WhatsAppParser {

    public record Event(
            String type,
            String phone,
            String text,
            String buttonId
    ) {}

    public static Event parse(Map<String, Object> payload) {

        try {
            List<Map<String, Object>> entry =
                    (List<Map<String, Object>>) payload.get("entry");

            Map<String, Object> value =
                    (Map<String, Object>) ((List<Map<String, Object>>)
                            entry.get(0).get("changes"))
                            .get(0)
                            .get("value");

            List<Map<String, Object>> messages =
                    (List<Map<String, Object>>) value.get("messages");

            Map<String, Object> message = messages.get(0);

            String from = message.get("from").toString();
            String type = message.get("type").toString();

            // ✅ BUTTON CLICK
            if ("interactive".equals(type)) {

                Map<String, Object> interactive =
                        (Map<String, Object>) message.get("interactive");

                Map<String, Object> buttonReply =
                        (Map<String, Object>) interactive.get("button_reply");

                String buttonId = buttonReply.get("id").toString();

                return new Event(
                        "BUTTON",
                        from,
                        null,
                        buttonId
                );
            }

            // ✅ TEXT MESSAGE
            if ("text".equals(type)) {

                Map<String, Object> text =
                        (Map<String, Object>) message.get("text");

                return new Event(
                        "TEXT",
                        from,
                        text.get("body").toString(),
                        null
                );
            }

            // ✅ IMAGE MESSAGE
            if ("image".equals(type)) {
                return new Event(
                        "IMAGE",
                        from,
                        null,
                        null
                );
            }

        } catch (Exception e) {
            return null;
        }

        return null;
}}