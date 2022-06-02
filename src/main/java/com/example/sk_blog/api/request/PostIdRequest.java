package com.example.sk_blog.api.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class PostIdRequest {
    @JsonProperty("post_id")
    private Integer postId;
}
