package com.example.sk_blog.repositories;

import com.example.sk_blog.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Integer> {


}
