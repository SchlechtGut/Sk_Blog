package com.example.sk_blog.model;

import com.example.sk_blog.model.enums.ModerationStatus;
import com.example.sk_blog.service.PostService;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;


@Entity
@Table(name = "posts")
@Getter
@Setter
@JsonPropertyOrder({ "id", "timestamp", "user", "title", "announce", "likeCount", "dislikeCount", "commentCount", "viewCount" })
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "is_active", columnDefinition = "tinyint")
    @JsonIgnore
    private Integer isActive;

    @Column(name = "moderation_status", columnDefinition = "varchar(50) default 'NEW'")
    @JsonIgnore
    private ModerationStatus moderationStatus;

    @Column(name = "moderator_id")
    @JsonIgnore
    private Integer moderatorId;

//    @JsonFormat(shape = JsonFormat.Shape.NUMBER)
    @JsonSerialize(using = PostService.CustomDateSerializer.class)
    @JsonProperty("timestamp")
    private LocalDateTime time;

    @NotNull
    private String title;

    @Column(columnDefinition = "text")
    @NotNull
    @JsonIgnore
    private String text;

    @Column(name = "view_count")
    @NotNull
    private int viewCount;

    @ManyToOne
    @JoinColumn(name="user_id", nullable=false)
    private User user;

    @OneToMany(mappedBy = "post")
    @JsonIgnore
    private Set<PostVote> postVotes;

    @OneToMany(mappedBy = "post")
    @JsonIgnore
    private Set<PostComment> postComments;

    @ManyToMany(mappedBy = "posts")
    @JsonIgnore
    private Set<Tag> tags;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Post post = (Post) o;
        return id == post.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public long getLikesCount() {
        return postVotes.stream().filter(x -> x.getValue() == 1).count();
    }

    public long getDislikesCount() {
        return postVotes.stream().filter(x -> x.getValue() == -1).count();
    }
}
