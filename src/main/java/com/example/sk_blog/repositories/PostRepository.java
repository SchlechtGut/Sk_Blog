package com.example.sk_blog.repositories;

import com.example.sk_blog.model.Post;
import com.example.sk_blog.model.PostVote;
import com.example.sk_blog.model.enums.ModerationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

public interface PostRepository extends JpaRepository<Post, Integer> {

    Page<Post> getPostByIsActiveEqualsAndModerationStatusEquals(Integer isActive, @NotNull ModerationStatus moderationStatus, Pageable pageable);

    List<Post> getAllByTimeBefore(LocalDateTime time);
}
