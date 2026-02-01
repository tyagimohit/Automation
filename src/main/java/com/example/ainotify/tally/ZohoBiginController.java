package com.example.ainotify.tally;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ZohoBiginController {

    @Autowired
    private ZohoBiginService zohoBiginService;


    @GetMapping("/addNotes")
    public String addNotes() {
        List<String> companiesIds = zohoBiginService.addCompanyNotes("Notes added from API");
        return "Notes added for companies : "+companiesIds;
    }

}
