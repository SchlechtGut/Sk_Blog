package com.example.sk_blog.api.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CaptchaResponse {

    private String secret;
    private String image;
}
