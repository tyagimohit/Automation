package com.example.ainotify.readwhatsapp.service;

import com.example.ainotify.readwhatsapp.entity.Image;
import com.example.ainotify.readwhatsapp.entity.Message;
import com.example.ainotify.readwhatsapp.entity.WhatsAppApiClient;
import com.example.ainotify.readwhatsapp.entity.WhatsAppMediaEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class WhatsAppReadMessageService {

    @Autowired
    public WhatsAppApiClient whatsappApiClient;

    public String process(Map<String, Object> payload) {
        if (payload == null) return "";

        Map entry = ((List<Map>) payload.get("entry")).get(0);
        Map change = ((List<Map>) entry.get("changes")).get(0);
        Map value = (Map) change.get("value");

        List<Map> messages = (List<Map>) value.get("messages");
        if (messages == null) return "";

        Map message = messages.get(0);
        String type = (String) message.get("type");

        Message whatsAppMessage = new Message();

        whatsAppMessage.setType((String) message.get("type"));
        whatsAppMessage.setId((String) message.get("id"));
        whatsAppMessage.setFrom((String) message.get("from"));

        LinkedHashMap text = (LinkedHashMap) message.get("text");
        if(text!=null){
            whatsAppMessage.setText((String)text.get("body"));
        }

        LinkedHashMap whatsappimage = (LinkedHashMap) message.get("image");
        if(whatsappimage!=null){
            whatsAppMessage.setText((String)whatsappimage.get("image"));
        }

        Image image = new Image();
        image.setId((String)whatsappimage.get("id"));

        whatsAppMessage.setImage(image);
        whatsAppMessage.setTimestamp((String) message.get("timestamp"));

        if(type.equals("image")){
            return handleImage(whatsAppMessage);
        }

        return "";


//        switch (type) {
//            case "text" : handleText(whatsAppMessage);
//            break;
//
//            case "image": handleImage(whatsAppMessage);
//                break;
//        }
    }

    private void handleText(Message message) {
        System.out.println("WhatsApp message: "+message.getText());
        whatsappApiClient.markAsRead(message.getId());
    }

    private String handleImage(Message message) {
        Path filePath = null;
        try {
            String mediaId = message.getImage().getId();
            String from = message.getFrom();
            String caption = message.getImage().getCaption();

            // Download image
            byte[] imageBytes =
                    whatsappApiClient.downloadMedia(mediaId);

            // Save image
            Path dir = Path.of("images");
            Files.createDirectories(dir);

            filePath = dir.resolve(mediaId + ".jpg");
            Files.write(filePath, imageBytes);

            // Save metadata
            WhatsAppMediaEntity entity = new WhatsAppMediaEntity();
            entity.setMediaId(mediaId);
            entity.setFromNumber(from);
            entity.setType("image");
            entity.setCaption(caption);
            entity.setFilePath(filePath.toString());
            entity.setReceivedAt(Instant.now());

            // Reply
            whatsappApiClient.sendTextMessage(
                    from,
                    "📸 Image received and saved successfully!"
            );

            whatsappApiClient.markAsRead(message.getId());

        } catch (Exception e) {
            e.printStackTrace();
        }

        return filePath.toAbsolutePath().toString();
    }

}