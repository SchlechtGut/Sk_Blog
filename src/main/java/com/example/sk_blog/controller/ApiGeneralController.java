package com.example.sk_blog.controller;

import com.example.sk_blog.api.response.CalendarResponse;
import com.example.sk_blog.api.response.InitResponse;
import com.example.sk_blog.api.response.SettingsResponse;
import com.example.sk_blog.api.response.TagResponse;
import com.example.sk_blog.service.ApiGeneralService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ApiGeneralController {

    private final ApiGeneralService apiGeneralService;

    @Autowired
    public ApiGeneralController(ApiGeneralService apiGeneralService) {
        this.apiGeneralService = apiGeneralService;
    }

    @GetMapping("/init")
    public InitResponse init() {
        return apiGeneralService.getInitResponse();
    }

    @GetMapping("/settings")
    public SettingsResponse settings() {
        return apiGeneralService.getGlobalSettings();
    }

    @GetMapping("/tag")
    public TagResponse tags(@RequestParam(required = false) String query) {
        return apiGeneralService.getTags(query);
    }

    @GetMapping("/calendar")
    public CalendarResponse calendarData(@RequestParam(required = false) Integer year) {
        return apiGeneralService.getCalendarResponse(year);
    }
}
