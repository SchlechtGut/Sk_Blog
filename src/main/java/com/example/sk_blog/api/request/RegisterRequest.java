package com.example.sk_blog.api.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class RegisterRequest {

    @JsonProperty("e_mail")
    @Email
    private String email;
    @Size(min = 6, message = "Пароль короче 6-ти символов")
    private String password;
    @Size(min = 2)
    private String name;
    @NotBlank
    private String captcha;

    @NotBlank
    @JsonProperty("captcha_secret")
    private String captchaSecret;
}
