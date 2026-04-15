package com.example.ainotify.bigin;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ZohoTokenManager {

    @Value("${ZOHO_BIGIN_CLIENT_ID}")
    private String CLIENT_ID;

    @Value("${ZOHO_BIGIN_CLIENT_SECRET}")
    private String CLIENT_SECRET;

    @Value("${ZOHO_BIGIN_REFRESH_TOKEN}")
    private String REFRESH_TOKEN;

    @Value("${ZOHO_BIGIN_ACCOUNT_URL}")
    private String ACCOUNTS_URL;

    private static String accessToken;
    private static long expiryTimeMillis;

    // Call this before every API request
    public synchronized String getAccessToken() throws Exception {
        if (accessToken == null || isExpired()) {
            refreshAccessToken();
        }
        return accessToken;
    }

    private static boolean isExpired() {
        return System.currentTimeMillis() >= expiryTimeMillis;
    }

    private void refreshAccessToken() throws Exception {

        System.out.println("ACCOUNTS_URL: " + ACCOUNTS_URL);
        System.out.println("REFRESH_TOKEN: " + REFRESH_TOKEN);
        System.out.println("CLIENT_ID: " + CLIENT_ID);
        System.out.println("CLIENT_SECRET: " + CLIENT_SECRET);


        String urlStr = ACCOUNTS_URL
                + "?grant_type=refresh_token"
                + "&refresh_token=" + REFRESH_TOKEN
                + "&client_id=" + CLIENT_ID
                + "&client_secret=" + CLIENT_SECRET;

        HttpURLConnection con =
                (HttpURLConnection) new URL(urlStr).openConnection();

        con.setRequestMethod("POST");
        con.setConnectTimeout(10000);
        con.setReadTimeout(10000);

        int status = con.getResponseCode();
        if (status != 200) {
            throw new RuntimeException("Failed to refresh token. HTTP " + status);
        }

        BufferedReader reader =
                new BufferedReader(new InputStreamReader(con.getInputStream()));

        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();

        JSONObject json = new JSONObject(response.toString());

        accessToken = json.getString("access_token");
        int expiresIn = json.getInt("expires_in");

        // subtract 60 seconds buffer
        expiryTimeMillis = System.currentTimeMillis()
                + (expiresIn * 1000L) - 60_000;

        System.out.println("Zoho access token refreshed");
    }
}