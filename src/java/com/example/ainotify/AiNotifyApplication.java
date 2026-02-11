package com.example.ainotify;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AiNotifyApplication {

    public static void main(String[] args) {
        SpringApplication.run(AiNotifyApplication.class, args);
    }


}
