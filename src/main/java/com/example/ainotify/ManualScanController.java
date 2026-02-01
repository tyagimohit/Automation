package com.example.ainotify;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ManualScanController {

    private final DrivePollingService drivePollingService;

    @Autowired
    private WhatsAppService whatsAppService;

    @Autowired
    GmailService gmailService;

    public ManualScanController(DrivePollingService drivePollingService) {
        this.drivePollingService = drivePollingService;
    }

    @GetMapping("/scan-now")
    public String scanNow() {
        Task task = drivePollingService.scanTasks();
        return "Scan triggered: for phone no.: "+task.getPhone()+"--title: "+task.getTitle()+"---time: "+task.getReminderTime();
    }

    @GetMapping("/addNotesFromMail")
    public String addNotesFromMail() throws Exception {
        List<GmailMessageResponse> gmailMessageList = gmailService.getUnreadMessages();
        return "Notes added count: "+gmailMessageList.size();
    }

    @GetMapping("/test-whatsapp")
    public String test() {
        whatsAppService.sendReminder(
                "919873816478",
                "✅ WhatsApp integration working"
        );

//        whatsAppService.sendFestivalOffer(
//                "919873816478",
//                "Mohit",
//                "Glow Salon",
//                "Flat 15% OFF on Haircut & Facial",
//                "15 Nov 2026"
//        );

        return "Sent";
    }
}
