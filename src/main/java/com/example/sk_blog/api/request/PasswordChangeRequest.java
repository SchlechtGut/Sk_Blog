package com.example.sk_blog.api.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class PasswordChangeRequest {

    private String code;
    private String password;
    private String captcha;
    @JsonProperty("captcha_secret")
    private String captchaSecret;
}
