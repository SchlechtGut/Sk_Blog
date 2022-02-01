package com.example.sk_blog.controller;

import com.example.sk_blog.api.response.AuthProperties;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/api/auth/")
public class ApiAuthController {

    @GetMapping("check")
    @ResponseBody
    public AuthProperties authCheck() {
        return new AuthProperties(false);
    }
}
