package com.example.sk_blog.controller;

import com.example.sk_blog.api.request.RegisterRequest;
import com.example.sk_blog.api.response.AuthResponse;
import com.example.sk_blog.api.response.CaptchaResponse;
import com.example.sk_blog.api.response.RegisterResponse;
import com.example.sk_blog.service.ApiAuthService;
import com.example.sk_blog.service.ApiGeneralService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

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
