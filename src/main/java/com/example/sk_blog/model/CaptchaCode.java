package com.example.sk_blog.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Entity
@Table(name = "captcha_codes")
public class CaptchaCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotNull
    private Date time;

    @Column(columnDefinition = "tinytext")
    @NotNull
    private String code;

    @Column(name = "secret_code", columnDefinition = "tinytext")
    @NotNull
    private String secretCode;
}
