package com.example.sk_blog.api.response;

import com.example.sk_blog.model.User;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthProperties {
    private boolean result;
    private User user;

    public AuthProperties(boolean result) {
        this.result = result;
    }

    public AuthProperties(boolean result, User user) {
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
