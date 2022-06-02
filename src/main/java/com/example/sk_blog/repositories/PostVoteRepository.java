package com.example.sk_blog.repositories;

import com.example.sk_blog.model.Post;
import com.example.sk_blog.model.PostVote;
import com.example.sk_blog.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.validation.constraints.NotNull;

@Repository
public interface PostVoteRepository extends JpaRepository<PostVote, Integer> {

    boolean existsByValueAndPostAndUser(@NotNull int value, Post post, User user);

    PostVote findByValueAndPostAndUser(@NotNull int value, Post post, User user);
}
