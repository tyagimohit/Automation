package com.example.ainotify.gmail;

import com.example.ainotify.bigin.ZohoBiginService;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;

import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Service
public class GmailService {

    private Gmail gmail;

    @Autowired
    private ZohoBiginService zohoBiginService;

    private synchronized Gmail getGmail() throws Exception {
        if (gmail == null) {
            gmail = createGmailClient();
        }
        return gmail;
    }

    @Value("${gmail.client-id}")
    private String CLIENT_ID;

    @Value("${gmail.client-secret}")
    private String CLIENT_SECRET;

    @Value("${gmail.refresh-token}")
    private String REFRESH_TOKEN;

    private static final String APPLICATION_NAME = "AIAutomation";

    public Gmail createGmailClient() throws Exception {

        var httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        var jsonFactory = JacksonFactory.getDefaultInstance();

        GoogleCredential credential = new GoogleCredential.Builder()
                .setTransport(httpTransport)
                .setJsonFactory(jsonFactory)
                .setClientSecrets(CLIENT_ID, CLIENT_SECRET)
                .build();

        credential.setRefreshToken(REFRESH_TOKEN);

        return new Gmail.Builder(httpTransport, jsonFactory, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    public List<GmailMessageResponse> getUnreadMessages() throws Exception {

        List<GmailMessageResponse> gmailMessageResponseList = new ArrayList<>();
        Gmail service = getGmail();

        ListMessagesResponse resp = service.users()
                .messages()
                .list("me")
                .setQ("is:unread subject:\"demo-notes\" -subject:re -subject:fwd")
                .execute();

        List<Message> messageList = resp.getMessages();
        if(messageList!=null){
            for (Message msg : messageList) {
                Message fullMessage = gmail.users()
                        .messages()
                        .get("me", msg.getId())
                        .setFormat("full")
                        .execute();

                System.out.println("------------------------------------------------");

                System.out.println("Subject: " + getHeader(fullMessage, "Subject"));
                System.out.println("From   : " + getHeader(fullMessage, "From"));
                System.out.println("Date   : " + getHeader(fullMessage, "Date"));

                String body = getEmailBody(fullMessage);
                System.out.println("Body:\n" + body);

                List<String> list = zohoBiginService.addCompanyNotes(body);

                if(list.size()>=1){
                    System.out.println("Notes added succesfully: "+list);
                }

                GmailMessageResponse gmailMessageResponse = new GmailMessageResponse();
                gmailMessageResponse.setBody(body);
                gmailMessageResponse.setFrom(getHeader(fullMessage, "From"));
                gmailMessageResponse.setSubject(getHeader(fullMessage, "Subject"));
                gmailMessageResponse.setDate(getHeader(fullMessage, "Date"));
                gmailMessageResponseList.add(gmailMessageResponse);
            }
        }else{
            gmailMessageResponseList.add(new GmailMessageResponse());
        }

        return gmailMessageResponseList;
    }

    private static String getHeader(Message message, String name) {
        List<MessagePartHeader> headers = message.getPayload().getHeaders();
        for (MessagePartHeader header : headers) {
            if (name.equalsIgnoreCase(header.getName())) {
                return header.getValue();
            }
        }
        return "";
    }

    private static String getEmailBody(Message message) {

        MessagePart payload = message.getPayload();

        // Case 1: Simple body
        if (payload.getBody() != null && payload.getBody().getData() != null) {
            return decode(payload.getBody().getData());
        }

        // Case 2: Multipart
        if (payload.getParts() != null) {

            // Prefer text/plain
            for (MessagePart part : payload.getParts()) {
                if ("text/plain".equalsIgnoreCase(part.getMimeType())
                        && part.getBody() != null
                        && part.getBody().getData() != null) {
                    return decode(part.getBody().getData());
                }
            }

            // Fallback to text/html
            for (MessagePart part : payload.getParts()) {
                if ("text/html".equalsIgnoreCase(part.getMimeType())
                        && part.getBody() != null
                        && part.getBody().getData() != null) {
                    return decode(part.getBody().getData());
                }
            }
        }
        return "";
    }

    private static String decode(String encoded) {
        return new String(Base64.getUrlDecoder().decode(encoded));
    }
}
