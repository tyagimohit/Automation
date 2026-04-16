package com.example.ainotify.bigin;

import java.util.*;
import java.util.regex.*;

public class MessageParser {

    public static Map<String, String> parseMessage(String text) {

        Map<String, String> map = new LinkedHashMap<>();

        // Normalize text (important for Gmail content)
        text = text.replaceAll("\\r", "")
                .replaceAll("\\n+", "\n")
                .trim();

        // Split based on numbering or bullets
        String[] parts = text.split("(?=(\\d+\\.|[-•]))");

        for (String part : parts) {

            part = part.trim();
            if (part.isEmpty()) continue;

            // Extract number from company line
            String numberKey = extractOnlyNumber(part);

            if (numberKey != null) {
                // Remove first line (company name part)
                String value = part.replaceFirst("^(\\d+\\.|[-•])\\s*.*?\n?", "")
                        .trim()
                        .replaceAll("\\s+", " ");

                map.put(numberKey, value);
            }
        }

        return map;
    }

    private static String extractOnlyNumber(String input) {

        // (2) or [2]
        Matcher m1 = Pattern.compile("[\\(\\[]\\s*(\\d+)\\s*[\\)\\]]").matcher(input);
        if (m1.find()) return m1.group(1);

        // number at end
        Matcher m2 = Pattern.compile("(\\d+)\\s*$").matcher(input);
        if (m2.find()) return m2.group(1);

        return null;
    }
}