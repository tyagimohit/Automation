package com.example.ainotify.writeWhatsapp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WhatsappWriteController {

    @Autowired
    private WhatsAppService whatsAppService;

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
