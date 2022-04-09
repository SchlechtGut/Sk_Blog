package com.example.sk_blog.api.response;

import com.example.sk_blog.dto.PostDTO;
import com.example.sk_blog.model.Post;
import lombok.Data;

import java.util.List;

@Data
public class PostResponse {

    private final int count;
    private final List<PostDTO> posts;
}
