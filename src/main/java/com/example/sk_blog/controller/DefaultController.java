package com.example.sk_blog.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
@RequestMapping
public class DefaultController {

    @GetMapping
    public String home() {
        return "index";
    }

    @GetMapping(value="{path:.*}")
    public void redirect(HttpServletResponse response) throws IOException {
        response.sendRedirect("/");
    }
}
