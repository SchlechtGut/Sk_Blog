package com.example.sk_blog.controller;

import com.example.sk_blog.api.request.ModerationRequest;
import com.example.sk_blog.api.request.PostCommentRequest;
import com.example.sk_blog.api.response.*;
import com.example.sk_blog.service.ApiGeneralService;
import com.example.sk_blog.service.ApiPostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api")
public class ApiGeneralController {

    private final ApiGeneralService apiGeneralService;
    private final ApiPostService apiPostService;

    @Autowired
    public ApiGeneralController(ApiGeneralService apiGeneralService, ApiPostService apiPostService) {
        this.apiGeneralService = apiGeneralService;
        this.apiPostService = apiPostService;
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

    @PostMapping("/comment")
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<?> postComment(@RequestBody @Valid PostCommentRequest postCommentRequest, BindingResult bindingResult) {
        return apiPostService.addPostComment(postCommentRequest, bindingResult);
    }

    @PostMapping("/moderation")
    @PreAuthorize("hasAuthority('user:moderate')")
    public TrueOrErrorsResponse moderatePost(@RequestBody @Valid ModerationRequest moderationRequest) {
        return apiPostService.moderatePost(moderationRequest);
    }

    @GetMapping("/statistics/my")
    @PreAuthorize("hasAuthority('user:write')")
    public StatisticsResponse statisticsMy() {
        return apiGeneralService.getUserStatistics();
    }

    @GetMapping("/statistics/all")
    public ResponseEntity<?> statisticsAll() {
        return apiGeneralService.getGeneralStatistics();
    }
}
