package com.example.sk_blog.controller;

import com.example.sk_blog.api.request.EmailRequest;
import com.example.sk_blog.api.request.LoginRequest;
import com.example.sk_blog.api.request.PasswordChangeRequest;
import com.example.sk_blog.api.request.RegisterRequest;
import com.example.sk_blog.api.response.CaptchaResponse;
import com.example.sk_blog.api.response.LoginResponse;
import com.example.sk_blog.api.response.TrueOrErrorsResponse;
import com.example.sk_blog.service.ApiAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

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
        return apiAuthService.authCheck(principal);
    }

    @GetMapping("/captcha")
    public CaptchaResponse captcha() {
        return apiAuthService.getCaptcha();
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody @Valid RegisterRequest request, BindingResult bindingResult) {
        return apiAuthService.register(request, bindingResult);
    }

    @PostMapping("/restore")
    public TrueOrErrorsResponse restorePassword(@RequestBody EmailRequest emailRequest) {
        return apiAuthService.restorePassword(emailRequest);
    }

    @PostMapping("/password")
    public TrueOrErrorsResponse changePassword(@RequestBody PasswordChangeRequest passwordChangeRequest) {
        return apiAuthService.changePassword(passwordChangeRequest);
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
