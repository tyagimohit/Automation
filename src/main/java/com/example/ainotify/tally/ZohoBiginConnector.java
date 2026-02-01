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

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class ZohoBiginConnector {

    public static void main(String[] args) {
        try {
            String accessToken = "1000.4a4794f4912b1ebe844f27e282d836bb.8c1830dad44654eaf33a27541623823f";
            String getCompaniesListUrl = "https://www.zohoapis.in/bigin/v2/Accounts?fields=Account_Name";

            HttpURLConnection con = (HttpURLConnection) new URL(getCompaniesListUrl).openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("Authorization", "Zoho-oauthtoken " + accessToken);
            con.setRequestProperty("Accept", "application/json");

            int status = con.getResponseCode();
            InputStream stream;

            if (status >= 200 && status < 300) {
                stream = con.getInputStream();
            } else {
                stream = con.getErrorStream();
            }

            BufferedReader br = new BufferedReader(
                    new InputStreamReader(stream));

            String line;
            StringBuilder responseBuilder = new StringBuilder();
            while ((line = br.readLine()) != null) {
                responseBuilder.append(line);
            }

            JSONObject jsonObject = new JSONObject(responseBuilder.toString());

            JSONArray dataArray = jsonObject.getJSONArray("data");

            for (int i = 0; i < dataArray.length(); i++) {
                JSONObject company = dataArray.getJSONObject(i);
                String id = company.getString("id");
                addNotes(id, accessToken, "hello "+ LocalDateTime.now());
                System.out.println(id);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void addNotes(String accountId, String accessToken, String notes) throws IOException {

        String addNotesUrl = "https://www.zohoapis.in/bigin/v2/Notes";

        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        HttpPost post = new HttpPost(addNotesUrl);

        post.setHeader("Authorization", "Zoho-oauthtoken " + accessToken);
        post.setHeader("Content-Type", "application/json");

        LocalDate.now();

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
""", notes, accountId);

        post.setEntity(new StringEntity(body, ContentType.APPLICATION_JSON));

        try (CloseableHttpResponse response = httpClient.execute(post)) {

            String responseBody = EntityUtils.toString(response.getEntity());
            System.out.println("HTTP Status: " + response.getStatusLine());
            System.out.println("Response: " + responseBody);
        }
    }

}
