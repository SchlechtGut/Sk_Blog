package com.example.sk_blog.api.response;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.LinkedHashMap;
import java.util.TreeMap;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class RegisterResponse {

    private boolean result;
    private LinkedHashMap<String, String> errors;

    public RegisterResponse(boolean result, LinkedHashMap<String, String> errors) {
        this.result = result;
        this.errors = errors;
    }

    public RegisterResponse(boolean result) {
        this.result = result;
    }

    public RegisterResponse() {
    }

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public LinkedHashMap<String, String> getErrors() {
        return errors;
    }

    public void setErrors(LinkedHashMap<String, String> errors) {
        this.errors = errors;
    }
}
