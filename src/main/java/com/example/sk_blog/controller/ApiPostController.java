package com.example.sk_blog.controller;

import com.example.sk_blog.api.response.PostResponse;
import com.example.sk_blog.model.Post;
import com.example.sk_blog.service.ApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/post")
public class ApiPostController {

    private final ApiService apiService;

    @Autowired
    public ApiPostController(ApiService apiService) {
        this.apiService = apiService;
    }

    @GetMapping
    public PostResponse getPosts(@RequestParam Integer offset, @RequestParam Integer limit, @RequestParam(required = false, defaultValue = "recent") String mode) {
        return apiService.getPosts(offset, limit, mode);
    }

}
