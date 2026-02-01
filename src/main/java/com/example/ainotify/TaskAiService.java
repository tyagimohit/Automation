package com.example.ainotify;

import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Service
public class TaskAiService {

    private final ObjectMapper mapper = new ObjectMapper();

    public Task extractTask(String content) {
        try {
            // 👉 DEMO: Assume AI already returned JSON
            // Replace this later with real OpenAI API call

            String aiResponse = """
            {
              "task": "Follow up with client Rohit",
              "date": "2026-01-25",
              "time": "18:00",
              "priority": "high"
            }
            """;

            JsonNode node = mapper.readTree(aiResponse);

            Task task = new Task();
            task.setTitle(node.get("task").asText());
            task.setPriority(node.get("priority").asText());

            LocalDate date = LocalDate.parse(node.get("date").asText());
            LocalTime time = LocalTime.parse(node.get("time").asText());

            task.setReminderTime(LocalDateTime.now().plusSeconds(2));
            task.setPhone("919873816478");

            return task;

        } catch (Exception e) {
            throw new RuntimeException("AI extraction failed", e);
        }
    }
}

