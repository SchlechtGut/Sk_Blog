package com.example.sk_blog.controller;

import com.example.sk_blog.api.response.CalendarResponse;
import com.example.sk_blog.api.response.CaptchaResponse;
import com.example.sk_blog.api.response.PostResponse;
import com.example.sk_blog.dto.SinglePostDTO;
import com.example.sk_blog.service.ApiGeneralService;
import com.example.sk_blog.service.ApiPostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/post")
public class ApiPostController {

    private final ApiPostService apiPostService;

    @Autowired
    public ApiPostController(ApiPostService apiPostService) {
        this.apiPostService = apiPostService;
    }

    @GetMapping
    public PostResponse posts(@RequestParam Integer offset, @RequestParam Integer limit, @RequestParam(required = false, defaultValue = "recent") String mode) {
        return apiPostService.getPosts(offset, limit, mode);
    }

    @GetMapping("/search")
    public PostResponse searchedPosts(@RequestParam Integer offset, @RequestParam Integer limit, @RequestParam String query) {
        return apiPostService.getSearchedPosts(offset, limit, query);
    }

    @GetMapping("/byDate")
    public PostResponse postsByDate(@RequestParam Integer offset, @RequestParam Integer limit, @RequestParam(required = false) String date) {
        return apiPostService.getPostsByDate(offset, limit, date);
    }

    @GetMapping("/byTag")
    public PostResponse postsByTag(@RequestParam Integer offset, @RequestParam Integer limit, @RequestParam String tag) {
        return apiPostService.getPostsByTag(offset, limit, tag);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SinglePostDTO> postById(@PathVariable Integer id) {
        return apiPostService.getPostById(id);
    }




}
