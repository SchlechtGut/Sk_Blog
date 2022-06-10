package com.example.sk_blog.api.request;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.Size;

@Data
public class ProfileRequest {
    @Size(min = 2, message = "имя менее 2 символов")
    private String name;
    private String password;
    @Email
    private String email;
    private Integer removePhoto;
}
