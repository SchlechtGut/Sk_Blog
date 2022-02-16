package com.example.sk_blog.api.response;

import com.example.sk_blog.model.User;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthResponse {
    private boolean result;
    private User user;

    public AuthResponse(boolean result) {
        this.result = result;
    }

    public AuthResponse(boolean result, User user) {
        this.result = result;
        this.user = user;
    }

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
