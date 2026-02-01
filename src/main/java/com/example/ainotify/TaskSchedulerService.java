package com.example.ainotify;


import org.quartz.*;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.util.Date;

@Service
public class TaskSchedulerService {

    private final Scheduler scheduler;

    public TaskSchedulerService(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    public void schedule(Task task) {
        try {
            JobDetail job = JobBuilder.newJob(WhatsAppReminderJob.class)
                    .withIdentity("job-" + System.currentTimeMillis())
                    .usingJobData("task", task.getTitle())
                    .usingJobData("phone", task.getPhone())
                    .build();

            Date triggerTime = Date.from(
                    task.getReminderTime()
                            .atZone(ZoneId.systemDefault())
                            .toInstant()
            );

            Trigger trigger = TriggerBuilder.newTrigger()
                    .startAt(triggerTime)
                    .build();

            scheduler.scheduleJob(job, trigger);

        } catch (Exception e) {
            throw new RuntimeException("Scheduling failed", e);
        }
    }
}

