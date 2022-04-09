package com.example.sk_blog.controller;

import com.example.sk_blog.api.request.LoginRequest;
import com.example.sk_blog.api.request.RegisterRequest;
import com.example.sk_blog.api.response.CaptchaResponse;
import com.example.sk_blog.api.response.LoginResponse;
import com.example.sk_blog.api.response.RegisterResponse;
import com.example.sk_blog.repositories.UserRepository;
import com.example.sk_blog.service.ApiAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.security.Principal;

@RestController
@RequestMapping("/api/auth")
public class ApiAuthController {

    private final ApiAuthService apiAuthService;

    @Autowired
    public ApiAuthController(ApiAuthService apiAuthService) {
        this.apiAuthService = apiAuthService;
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest loginRequest) {
        return apiAuthService.login(loginRequest);
    }

    @GetMapping("/check")
    public LoginResponse authCheck(Principal principal) {
        System.out.println(principal);
        return apiAuthService.authCheck(principal);
    }

    @GetMapping("/captcha")
    public CaptchaResponse captcha() {
        return apiAuthService.getCaptcha();
    }

    @PostMapping("/register")
    public RegisterResponse register(@RequestBody @Valid RegisterRequest request, BindingResult bindingResult) {
        return apiAuthService.register(request, bindingResult);
    }

//    @GetMapping("/logout")
//    public String handleLogout(HttpServletRequest request) {
//        HttpSession session = request.getSession();
//        SecurityContextHolder.clearContext();
//        if (session != null) {
//            session.invalidate();
//        }
//
//        for (Cookie cookie : request.getCookies()) {
//            cookie.setMaxAge(0);
//        }
//
//        return "redirect:/";
//    }
}
