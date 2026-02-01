package com.example.ainotify;


import com.google.api.services.drive.model.File;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DrivePollingService {

    private final TaskAiService aiService;
    private final TaskSchedulerService schedulerService;

    @Autowired
    GoogleDriveService googleDriveService;

    @Autowired
    GmailService gmailService;

    public DrivePollingService(TaskAiService aiService,
                               TaskSchedulerService schedulerService) {
        this.aiService = aiService;
        this.schedulerService = schedulerService;
    }

    public Task scanTasks() {
        try {
            List<GmailMessageResponse> gmailMessageList = gmailService.getUnreadMessages();
            System.out.println("gmailMessage count: " + gmailMessageList.size());

            for(GmailMessageResponse gmailMessage : gmailMessageList){

            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        String fileContent = "Test content";

        Task task = aiService.extractTask(fileContent);
        schedulerService.schedule(task);
        return task;
    }
}
