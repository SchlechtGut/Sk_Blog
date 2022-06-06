package com.example.sk_blog.api.request;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.Email;

@Data
public class ProfileRequest {
    private String name;
    private String password;
    private MultipartFile photo;
    @Email
    private String email;
    private Integer removePhoto;
}
