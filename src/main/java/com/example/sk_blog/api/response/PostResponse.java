package com.example.sk_blog.api.response;

import com.example.sk_blog.dto.PostDTO;
import com.example.sk_blog.model.Post;

import java.util.List;

public class PostResponse {

    private final int count;
    private final List<PostDTO> posts;

    public PostResponse(int count, List<PostDTO> posts) {
        this.count = count;
        this.posts = posts;
    }

    public int getCount() {
        return count;
    }

    public List<PostDTO> getPosts() {
        return posts;
    }
}
