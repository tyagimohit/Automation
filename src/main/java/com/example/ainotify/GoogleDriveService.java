package com.example.ainotify;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.ByteArrayContent;
import com.google.api.client.http.InputStreamContent;
import com.google.api.client.http.javanet.NetHttpTransport;

import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service
public class GoogleDriveService {

    private static final String APPLICATION_NAME = "AI Reminder Demo";
    private static final JacksonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    // 🔴 Path to service account JSON
    private static final String SERVICE_ACCOUNT_JSON = "/Users/mohit/Downloads/future-alcove-268117-b4efa3601455.json";

    private final Drive drive;

    public GoogleDriveService() throws Exception {
        NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();

        GoogleCredential credential = GoogleCredential
                .fromStream(new FileInputStream(SERVICE_ACCOUNT_JSON))
                .createScoped(Collections.singleton(DriveScopes.DRIVE));

        this.drive = new Drive.Builder(httpTransport, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    // 🔹 Read files from a folder
    public List<File> getFilesFromFolder(String folderId) throws Exception {

        String query = "'" + folderId + "' in parents and trashed=false";

        FileList result = drive.files()
                .list()
                .setQ(query)
                .setFields("files(id, name, description, createdTime)")
                .execute();

        return result.getFiles();
    }

    public String saveNotesInDrive(String notes) throws Exception {
        return saveStringToFolder(notes, LocalDateTime.now()+"", "13Q6-1ivlXmGoXfIVOHplIAt-EOV6V6mj");
    }

    private static Drive createDriveService() throws Exception {

        GoogleCredential credential = GoogleCredential.fromStream(
                        new FileInputStream(SERVICE_ACCOUNT_JSON))
                .createScoped(Collections.singleton("https://www.googleapis.com/auth/drive"));

        return new Drive.Builder(
                new NetHttpTransport(),
                JacksonFactory.getDefaultInstance(),
                credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    public String saveStringToFolder(String data, String fileName, String folderId) throws Exception {

        // Set file metadata with parent folder
//        File fileMetadata = new File();
//        fileMetadata.setName(fileName);
//        fileMetadata.setParents(Collections.singletonList(folderId));

        // Convert String to byte content
        ByteArrayContent content = ByteArrayContent.fromString("text/plain", data);

        // Upload to Drive
//        File uploadedFile = drive.files()
//                .create(fileMetadata, content)
//                .setFields("id, name, webViewLink")
//                .execute();

        File metadata = new File();
        metadata.setName(fileName+".txt");
        metadata.setParents(Collections.singletonList(folderId));

        Drive.Files.Create create =
                drive.files().create(metadata, content)
                        .setSupportsAllDrives(true)
                        .setFields("id,name,webViewLink");

        File uploaded = create.execute();


        return uploaded.getId();
    }

}