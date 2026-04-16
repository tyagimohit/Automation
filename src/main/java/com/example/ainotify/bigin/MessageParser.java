package com.example.ainotify.bigin;

import java.util.*;
import java.util.regex.*;

public class MessageParser {

    public static Map<String, String> parseMessage(String text) {

        Map<String, String> map = new LinkedHashMap<>();

        Pattern pattern = Pattern.compile(
                "(?:\\d+\\.|[-•])\\s*(.*?)\\s*(.*?)(?=(?:\\n(?:\\d+\\.|[-•]))|$)",
                Pattern.DOTALL
        );

        Matcher matcher = pattern.matcher(text);

        while (matcher.find()) {

            String rawKey = matcher.group(1).trim();
            String value = matcher.group(2).trim().replaceAll("\\s+", " ");

            String numberKey = extractOnlyNumber(rawKey);

            // ✅ Only add if number exists
            if (numberKey != null) {
                map.put(numberKey, value);
            }
        }

        return map;
    }

    // 🔥 Extract ONLY number, otherwise return null
    private static String extractOnlyNumber(String input) {

        // (2) or [2]
        Matcher m1 = Pattern.compile("[\\(\\[]\\s*(\\d+)\\s*[\\)\\]]").matcher(input);
        if (m1.find()) {
            return m1.group(1);
        }

        // number at end: "Company 2"
        Matcher m2 = Pattern.compile("(\\d+)\\s*$").matcher(input);
        if (m2.find()) {
            return m2.group(1);
        }

        return null; // ❌ skip if no number found
    }
}