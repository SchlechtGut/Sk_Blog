package com.example.sk_blog.api.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

@Data
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TrueOrErrorsResponse {

    private boolean result;
    private Map<String, String> errors;

    public TrueOrErrorsResponse(Map<String, String> errors) {
        this.errors = errors;
    }

    public TrueOrErrorsResponse(boolean result) {
        this.result = result;
    }

    public TrueOrErrorsResponse() {

    }
}
