package com.example.sk_blog.api.response;

import com.example.sk_blog.model.Post;

import java.util.List;

public class PostResponse {

    private int count;

    private List<Post> posts;

    public PostResponse(List<Post> posts) {
        this.posts = posts;
        count = posts.size();
    }

    public PostResponse(int count, List<Post> posts) {
        this.count = count;
        this.posts = posts;
    }

    public int getCount() {
        return count;
    }

    public List<Post> getPosts() {
        return posts;
    }
}
