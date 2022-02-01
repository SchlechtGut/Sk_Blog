package com.example.sk_blog.service;

import com.example.sk_blog.repositories.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PostService {

    PostRepository postRepository;

    @Autowired
    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

//    public Page<Post> getPageOfPosts() {
//
//    }
}
