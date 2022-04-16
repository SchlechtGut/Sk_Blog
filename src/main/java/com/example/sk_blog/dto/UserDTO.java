package com.example.sk_blog.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class UserDTO {
    private int id;
    private String name;
    private String photo;

    public UserDTO() {
    }

}
