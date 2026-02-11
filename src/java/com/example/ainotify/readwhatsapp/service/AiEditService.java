package com.example.ainotify.readwhatsapp.service;

import com.example.ainotify.readwhatsapp.entity.InvoiceDraft;
import org.springframework.stereotype.Service;

@Service
public class AiEditService {

    public InvoiceDraft apply(InvoiceDraft d, String text) {

        d.setItemsJson("[{\"item\":\"Parle G\",\"qty\":10,\"rate\":10}]");
        d.setTotalAmount(118.0);
        return d;
    }
}