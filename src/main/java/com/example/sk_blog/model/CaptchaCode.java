package com.example.sk_blog.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "captcha_codes")
public class CaptchaCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(columnDefinition = "tinytext")
    @NotNull
    private String code;

    @Column(name = "secret_code", columnDefinition = "tinytext")
    @NotNull
    private String secretCode;

    private LocalDateTime time;

    public CaptchaCode(String code, String secretCode, LocalDateTime time) {
        this.code = code;
        this.secretCode = secretCode;
        this.time = time;
    }

    public CaptchaCode() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getSecretCode() {
        return secretCode;
    }

    public void setSecretCode(String secretCode) {
        this.secretCode = secretCode;
    }
}
