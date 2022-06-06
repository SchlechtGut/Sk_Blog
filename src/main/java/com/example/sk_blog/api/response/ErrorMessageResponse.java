package com.example.sk_blog.api.response;

import lombok.Data;

@Data
public class ErrorMessageResponse {
    private String message;

    public ErrorMessageResponse(String message) {
        this.message = message;
    }
}
