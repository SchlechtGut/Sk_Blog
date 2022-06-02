package com.example.sk_blog.repositories;

import com.example.sk_blog.model.PostComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface PostCommentRepository extends JpaRepository<PostComment, Integer> {
}
