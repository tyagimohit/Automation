package com.example.ainotify.tally;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONObject;

public class ZohoTokenManager {

    private static final String CLIENT_ID = "1000.EREG8IONED2SRWHTNSNLF1I860NKOQ";
    private static final String CLIENT_SECRET = "076d8632649ffcf1a9128f7aba6739498873519fba";
    private static final String REFRESH_TOKEN = "1000.908c8a8757ee2ea1e48b89e6f32d627c.6d77f51b3b8a4f957a07b2eef4d26194";
    private static final String ACCOUNTS_URL = "https://accounts.zoho.in/oauth/v2/token";

    private static String accessToken;
    private static long expiryTimeMillis;

    // Call this before every API request
    public static synchronized String getAccessToken() throws Exception {
        if (accessToken == null || isExpired()) {
            refreshAccessToken();
        }
        return accessToken;
    }

    private static boolean isExpired() {
        return System.currentTimeMillis() >= expiryTimeMillis;
    }

    private static void refreshAccessToken() throws Exception {

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