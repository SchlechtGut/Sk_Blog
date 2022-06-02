package com.example.sk_blog.api.response;

public class ErrorMessageResponse {

    public String message;

    public ErrorMessageResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

}
