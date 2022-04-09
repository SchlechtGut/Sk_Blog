package com.example.sk_blog.api.response;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "api.init")
public class InitResponse {
    String title;
    String subtitle;
    String phone;
    String email;
    String copyright;
    String copyrightFrom;
}
