package com.example.ainotify.openAI;


import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;

public class OpenAIJavaExample {

    @Value("${openai.hf-api-key}")
    private static String HF_API_KEY;
    private static final String MODEL = "mistralai/Mistral-7B-Instruct-v0.1"; // Example model

    public static void main(String[] args) throws IOException {

        String prompt = "Explain how to extract Gmail unread messages in Java, simply.";

        OkHttpClient client = new OkHttpClient();

        String json = "{ \"inputs\": \"" + prompt.replace("\"", "\\\"") + "\" }";

        Request request = new Request.Builder()
                .url("https://api-inference.huggingface.co/pipeline/text-generation/" + MODEL)
                .addHeader("Authorization", "Bearer " + HF_API_KEY)
                .addHeader("Content-Type", "application/json")
                .post(RequestBody.create(json, MediaType.parse("application/json")))
                .build();

        Response response = client.newCall(request).execute();


        // Print output text
        System.out.println("------body-------:"+response.body().string());
    }
}
