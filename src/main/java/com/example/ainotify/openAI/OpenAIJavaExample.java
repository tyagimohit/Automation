package com.example.ainotify.openAI;


import okhttp3.*;

import java.io.IOException;

public class OpenAIJavaExample {

    private static String OPENAI_API_KEY = "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";


    private static String HF_API_KEY = "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";
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
