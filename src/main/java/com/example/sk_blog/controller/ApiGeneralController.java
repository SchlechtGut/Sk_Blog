package com.example.sk_blog.controller;

import com.example.sk_blog.api.response.InitResponse;
import com.example.sk_blog.api.response.GlobalSettingsProperty;
import com.example.sk_blog.service.ApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/")
public class ApiGeneralController {

    ApiService apiService;

    @Autowired
    public ApiGeneralController(ApiService apiService) {
        this.apiService = apiService;
    }

    @GetMapping("init")
    public InitResponse init() {
        return apiService.getApiInitProviderProperties();
    }

    @GetMapping("settings")
    public GlobalSettingsProperty settings() {
        return apiService.getGlobalSettings();
    }
}
