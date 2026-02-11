package com.example.ainotify.readwhatsapp.service;

import com.example.ainotify.readwhatsapp.entity.InvoiceDraft;
import org.springframework.stereotype.Service;

@Service
public class TallyService {

    public String createInvoice(InvoiceDraft d) {

        String xml =
                "<ENVELOPE>" +
                        "<BODY><IMPORTDATA><REQUESTDATA>" +
                        "<TALLYMESSAGE><VOUCHER VCHTYPE='Sales'>" +
                        "<PARTYNAME>" + d.getCustomerName() + "</PARTYNAME>" +
                        "</VOUCHER></TALLYMESSAGE>" +
                        "</REQUESTDATA></IMPORTDATA></BODY>" +
                        "</ENVELOPE>";

        System.out.println(xml);
        return "INV-" + System.currentTimeMillis();
    }
}