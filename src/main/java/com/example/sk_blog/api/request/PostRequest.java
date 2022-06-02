package com.example.sk_blog.api.request;

import lombok.Data;

import javax.validation.constraints.Size;
import java.util.Set;

@Data
public class PostRequest {
    private long timestamp;
    private int active;
    @Size(min = 3, message = "Заголовок должен быть не менее 3 символов")
    private String title;
    private Set<String> tags;
    @Size(min = 50, message = "Текст публикации слишком короткий")
    private String text;
}
