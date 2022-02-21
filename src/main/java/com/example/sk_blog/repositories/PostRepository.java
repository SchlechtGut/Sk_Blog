package com.example.sk_blog.repositories;

import com.example.sk_blog.model.Post;
import com.example.sk_blog.model.Tag;
import com.example.sk_blog.model.enums.ModerationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public interface PostRepository extends JpaRepository<Post, Integer> {

    Page<Post> findPostByIsActiveEqualsAndModerationStatusEqualsAndTimeBefore(Integer isActive, @NotNull ModerationStatus moderationStatus, LocalDateTime time, Pageable pageable);

    List<Post> findAllByTimeBefore(LocalDateTime time);

    Page<Post> findPostByTitleContainingIgnoreCaseAndIsActiveAndModerationStatusAndTimeBefore(@NotNull String title, Integer isActive, @NotNull ModerationStatus moderationStatus, LocalDateTime time, Pageable pageable);

    @Query(value = "SELECT DISTINCT Year(time) FROM posts ORDER BY Year(time) ASC", nativeQuery = true)
    List<Integer> findYears();

    @Query(value = "select cast(time as date) as date, count(1) as count from posts WHERE Year(time)=?1 GROUP BY cast(time as date)", nativeQuery = true)
    List<String> getMapOfDistinctPostDates(Integer year);

    @Query(value = "select * from posts WHERE date(time)=?1  and time <= ?2 and is_active=1 and moderation_status='ACCEPTED'", nativeQuery = true)
    Page<Post> findActiveAcceptedPostsByDate(LocalDate searchedDate, LocalDateTime presentTime, Pageable pageable);

    @Query(value = "SELECT * FROM posts p " +
            "LEFT JOIN tag2post tp ON tp.post_id = p.id " +
            "LEFT JOIN tags t ON t.id = tp.tag_id " +
            "WHERE t.name = ?1  and time <= ?2 and moderation_status='ACCEPTED' and is_active=1", nativeQuery = true)
    Page<Post> findByTagsAndModerationStatusAndIsActiveAndTimeBefore(String tag , LocalDateTime presentTime, Pageable pageable);
}
