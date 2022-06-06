package com.example.sk_blog.controller;

import com.example.sk_blog.api.response.TrueOrErrorsResponse;
import org.apache.tomcat.util.http.fileupload.impl.SizeLimitExceededException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.LinkedHashMap;
import java.util.Map;

@ControllerAdvice
public class NotFoundHandler {
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<String> renderDefaultPage() {
        try {
            File indexFile = ResourceUtils.getFile("classpath:public/index.html");
            FileInputStream inputStream = new FileInputStream(indexFile);
            String body = StreamUtils.copyToString(inputStream, Charset.defaultCharset());
            return ResponseEntity.ok().contentType(MediaType.TEXT_HTML).body(body);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("There was an error completing the action.");
        }
    }

    @ExceptionHandler(SizeLimitExceededException.class)
    public ResponseEntity<?> sizeExceeded() {
        Map<String, String> errors = new LinkedHashMap<>();
        errors.put("image", "Размер файла превышает допустимый размер");
        System.out.println("caught");
        return ResponseEntity.badRequest().body(new TrueOrErrorsResponse(errors));
    }
}
