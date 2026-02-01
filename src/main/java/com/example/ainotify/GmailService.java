package com.example.ainotify;

import com.example.ainotify.tally.ZohoBiginService;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;

import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.gmail.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.List;

@Service
public class GmailService {

    private static final String APPLICATION_NAME = "AI Notify";
    private static final List<String> SCOPES =
            List.of(
                    GmailScopes.GMAIL_READONLY, GmailScopes.GMAIL_MODIFY
            );

    private static final String SERVICE_ACCOUNT_JSON = "/Users/mohit/Downloads/client_secret_gmail.json";

    private Gmail gmail;

    @Autowired
    GoogleDriveService googleDriveService;

    @Autowired
    private ZohoBiginService zohoBiginService;

    private synchronized Gmail getGmail() throws Exception {
        if (gmail == null) {
            gmail = createGmailClient();
        }
        return gmail;
    }

    private Gmail createGmailClient() throws Exception {
        FileInputStream fileInputStream = new FileInputStream(SERVICE_ACCOUNT_JSON);

        if (fileInputStream == null) {
            throw new RuntimeException("credentials.json not found");
        }

        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(
                JacksonFactory.getDefaultInstance(),
                new InputStreamReader(fileInputStream)
        );

        GoogleAuthorizationCodeFlow flow =
                new GoogleAuthorizationCodeFlow.Builder(
                        GoogleNetHttpTransport.newTrustedTransport(),
                        JacksonFactory.getDefaultInstance(),
                        clientSecrets,
                        SCOPES)
                        .setDataStoreFactory(
                                new FileDataStoreFactory(new java.io.File("tokens")))
                        .setAccessType("offline")
                        .build();

        Credential credential = new AuthorizationCodeInstalledApp(
                flow,
                new LocalServerReceiver.Builder().setPort(8888).build()
        ).authorize("user");

        return new Gmail.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                JacksonFactory.getDefaultInstance(),
                credential)
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

                String id = googleDriveService.saveNotesInDrive(body);
                System.out.println("Notes added succesfully in drive with id : "+id);

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
