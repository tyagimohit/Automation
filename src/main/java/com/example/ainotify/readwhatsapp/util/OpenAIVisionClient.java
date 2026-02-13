package com.example.ainotify.readwhatsapp.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class OpenAIVisionClient {

    @Value("${openai.hf-api-key}")
    private String OPENAI_API_KEY;


    //private static final String OPENAI_API_KEY = "YOUR_API_KEY";

    public String readImage(String base64Image) throws Exception {

        String payload = """
        {
          "model": "gpt-4.1-mini",
          "input": [
            {
              "role": "user",
              "content": [
                { "type": "input_text", "text": "Extract items, quantity, price, GST from this invoice image." },
                {
                  "type": "input_image",
                  "image_url": "data:image/jpeg;base64,%s"
                }
              ]
            }
          ]
        }
        """.formatted(base64Image);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.openai.com/v1/responses"))
                .header("Authorization", "Bearer " + OPENAI_API_KEY)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(payload))
                .build();

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response =
                client.send(request, HttpResponse.BodyHandlers.ofString());

        return response.body();
    }
}