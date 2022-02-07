package com.example.sk_blog.controller;

import com.example.sk_blog.api.response.InitResponse;
import com.example.sk_blog.api.response.SettingsResponse;
import com.example.sk_blog.api.response.TagResponse;
import com.example.sk_blog.service.ApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/")
public class ApiGeneralController {

    private final ApiService apiService;

    @Autowired
    public ApiGeneralController(ApiService apiService) {
        this.apiService = apiService;
    }

    @GetMapping("init")
    public InitResponse init() {
        return apiService.getInitResponse();
    }

    @GetMapping("settings")
    public SettingsResponse settings() {
        return apiService.getGlobalSettings();
    }

    @GetMapping("tag")
    public TagResponse tags(@RequestParam(required = false) String query) {
        return apiService.getTags(query);
    }
}
