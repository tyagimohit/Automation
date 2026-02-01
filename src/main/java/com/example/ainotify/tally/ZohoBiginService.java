package com.example.ainotify.tally;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ZohoBiginService {

    public List<String> addCompanyNotes(String message) {

        List<String> companyIds = new ArrayList<>();

        try {
            String accessToken = ZohoTokenManager.getAccessToken();
            String getCompaniesListUrl = "https://www.zohoapis.in/bigin/v2/Accounts?fields=Account_Name";

            HttpURLConnection con = (HttpURLConnection) new URL(getCompaniesListUrl).openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("Authorization", "Zoho-oauthtoken " + accessToken);
            con.setRequestProperty("Accept", "application/json");

            int status = con.getResponseCode();
            InputStream stream;

            if (status >= 200 && status < 300) {
                stream = con.getInputStream();

                BufferedReader br = new BufferedReader(
                        new InputStreamReader(stream));

                String line;
                StringBuilder responseBuilder = new StringBuilder();
                while ((line = br.readLine()) != null) {
                    responseBuilder.append(line);
                }

                JSONObject jsonObject = new JSONObject(responseBuilder.toString().trim());
                JSONArray dataArray = jsonObject.getJSONArray("data");

                for (int i = 0; i < dataArray.length(); i++) {
                    JSONObject company = dataArray.getJSONObject(i);
                    String id = company.getString("id");
                    addNotes(id, accessToken, "Notes added at: "+ LocalDateTime.now() +". "+message);
                    companyIds.add(id);
                }
            } else {
                stream = con.getErrorStream();
                System.out.println("Response: Error in zoho api: "+stream.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return companyIds;
    }

    private static void addNotes(String accountId, String accessToken, String notes) throws IOException {

        String addNotesUrl = "https://www.zohoapis.in/bigin/v2/Notes";

        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        HttpPost post = new HttpPost(addNotesUrl);

        post.setHeader("Authorization", "Zoho-oauthtoken " + accessToken);
        post.setHeader("Content-Type", "application/json");

        String body = String.format("""
{
  "data": [
                        {
                        "Note_Content": "%s",
                        "Parent_Id": "%s",
                        "$se_module": "Accounts"
                      }
          ]
}
""", escapeJson(notes), accountId);

        post.setEntity(new StringEntity(body, ContentType.APPLICATION_JSON));

        try (CloseableHttpResponse response = httpClient.execute(post)) {
            String responseBody = EntityUtils.toString(response.getEntity());
            System.out.println("HTTP Status: " + response.getStatusLine());
            System.out.println("Response: " + responseBody);
        }
    }


    private static String escapeJson(String value) {
        if (value == null) return "";
        return value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
    }

}
