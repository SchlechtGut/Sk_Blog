package com.example.sk_blog.api.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCaptcha() {
        return captcha;
    }

    public void setCaptcha(String captcha) {
        this.captcha = captcha;
    }

    public String getCaptchaSecret() {
        return captchaSecret;
    }

    public void setCaptchaSecret(String captchaSecret) {
        this.captchaSecret = captchaSecret;
    }

    @Override
    public String toString() {
        return "RegisterRequest{" +
                "email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", name='" + name + '\'' +
                ", captcha='" + captcha + '\'' +
                ", captchaSecret='" + captchaSecret + '\'' +
                '}';
    }
}
