package com.example.sk_blog;

import com.example.sk_blog.api.response.InitResponse;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(InitResponse.class)
public class SkBlogApplication {
    public static void main(String[] args) {
        SpringApplication.run(SkBlogApplication.class, args);
    }

}


