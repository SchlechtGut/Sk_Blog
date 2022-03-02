package com.example.sk_blog.controller;

import com.example.sk_blog.api.request.RegisterRequest;
import com.example.sk_blog.api.response.AuthResponse;
import com.example.sk_blog.api.response.CaptchaResponse;
import com.example.sk_blog.api.response.RegisterResponse;
import com.example.sk_blog.service.ApiAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class ApiAuthController {

    private final ApiAuthService apiAuthService;

    @Autowired
    public ApiAuthController(ApiAuthService apiAuthService) {
        this.apiAuthService = apiAuthService;
    }

    @GetMapping("/check")
    public AuthResponse authCheck() {
        return new AuthResponse(false);
    }

    @GetMapping("/captcha")
    public CaptchaResponse captcha() {
        return apiAuthService.getCaptcha();
    }

    @PostMapping("/register")
    public RegisterResponse register(@RequestBody @Valid RegisterRequest request, BindingResult bindingResult) {
        return apiAuthService.register(request, bindingResult);
    }

}
