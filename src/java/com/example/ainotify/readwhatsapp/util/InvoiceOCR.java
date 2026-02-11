package com.example.ainotify.readwhatsapp.util;

import net.sourceforge.tess4j.Tesseract;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;

@Service
public class InvoiceOCR {

    @Autowired
    OpenAIVisionClient openAIVisionClient;

    public String imageToBase64(String imagePath) throws Exception {
        byte[] imageBytes = Files.readAllBytes(Path.of(imagePath));
        return Base64.getEncoder().encodeToString(imageBytes);
    }

    public String getTextFromImage(String imageLocation) throws Exception {

        String imagetext = imageToBase64(imageLocation);

        String imagetextfinal = openAIVisionClient.readImage(imagetext);



        System.out.println("----- Extracted Invoice Text -----");
        System.out.println(imagetextfinal);
        return imagetextfinal;
    }
}