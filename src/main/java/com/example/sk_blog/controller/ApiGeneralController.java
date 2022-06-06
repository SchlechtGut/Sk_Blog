package com.example.sk_blog.controller;

import com.example.sk_blog.api.request.ModerationRequest;
import com.example.sk_blog.api.request.PostCommentRequest;
import com.example.sk_blog.api.response.*;
import com.example.sk_blog.service.ApiGeneralService;
import com.example.sk_blog.service.ApiPostService;
import com.example.sk_blog.service.ResourceStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;

@RestController
@RequestMapping("/api")
public class ApiGeneralController {

    private final ApiGeneralService apiGeneralService;
    private final ApiPostService apiPostService;
    private final ResourceStorage resourceStorage;

    @Autowired
    public ApiGeneralController(ApiGeneralService apiGeneralService, ApiPostService apiPostService, ResourceStorage resourceStorage) {
        this.apiGeneralService = apiGeneralService;
        this.apiPostService = apiPostService;
        this.resourceStorage = resourceStorage;
    }

    @GetMapping("/init")
    public InitResponse init() {
        return apiGeneralService.getInitResponse();
    }

    @GetMapping("/settings")
    public SettingsResponse settings() {
        return apiGeneralService.getGlobalSettings();
    }

    @PutMapping("/settings")
    @PreAuthorize("hasAuthority('user:moderate')")
    public void changeGlobalSettings(@RequestBody SettingsResponse settings) {
        apiGeneralService.changeGlobalSettings(settings);
    }

    @GetMapping("/tag")
    public TagResponse tags(@RequestParam(required = false) String query) {
        return apiGeneralService.getTags(query);
    }

    @GetMapping("/calendar")
    public CalendarResponse calendarData(@RequestParam(required = false) Integer year) {
        return apiGeneralService.getCalendarResponse(year);
    }

    @PostMapping("/image")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> uploadImage(@RequestParam MultipartFile image) throws IOException {
        return resourceStorage.saveNewBookImage(image);
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

    @PostMapping(path = "/profile/my") //, consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}
    public TrueOrErrorsResponse editProfile(@RequestParam(required = false) String name,
                                            @RequestParam(required = false) String email,
                                            @RequestParam(required = false) String password,
                                            @RequestParam(required = false) Integer removePhoto,
                                            @RequestParam(required = false) MultipartFile photo) {
        System.out.println(name);
        System.out.println(email);
        System.out.println(password);
        System.out.println(removePhoto);
        System.out.println(photo);

        return new TrueOrErrorsResponse();
    }

//    @PostMapping(path = "/profile/my")
//    public TrueOrErrorsResponse editProfile(@ModelAttribute ProfileRequest profileRequest) {
//        System.out.println(profileRequest); //все поля null
//        return new TrueOrErrorsResponse();
//    }

//    @PostMapping(path = "/profile/my", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
//    public TrueOrErrorsResponse editProfile(@RequestPart ProfileRequest profileRequest, @RequestPart MultipartFile photo) {
//        System.out.println(profileRequest); // тоже не работает
//        System.out.println(photo);
//        return new TrueOrErrorsResponse();
//    }


}
