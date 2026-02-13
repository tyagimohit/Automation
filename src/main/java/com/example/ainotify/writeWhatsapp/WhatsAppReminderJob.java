package com.example.ainotify.writeWhatsapp;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;

public class WhatsAppReminderJob implements Job {

    @Autowired
    private WhatsAppService whatsAppService;

    @Override
    public void execute(JobExecutionContext context) {

        String task = context.getMergedJobDataMap().getString("task");
        String phone = context.getMergedJobDataMap().getString("phone");

        String msg = "🔔 AI Reminder\n\nTask: " + task;

        //whatsAppService.sendReminder(phone, msg);

        whatsAppService.sendFestivalOffer(
                "919873816478",
                "Mohit",
                "Glow Salon",
                "Flat 15% OFF on Haircut & Facial",
                "15 Nov 2026"
        );
    }
}
