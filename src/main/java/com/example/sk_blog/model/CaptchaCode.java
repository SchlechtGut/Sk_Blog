package com.example.sk_blog.model;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "captcha_codes")
@Data
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
}
