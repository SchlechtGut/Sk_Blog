package com.example.sk_blog.api.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class PostCommentRequest {

    @JsonProperty("parent_id")
    private Integer parentId;
    @JsonProperty("post_id")
    @NotNull(message = "post_id is not set")
    private Integer postId;
    @NotBlank(message = "text is not set")
    @Size(min = 3, message = "text is too short")
    private String text;
}
