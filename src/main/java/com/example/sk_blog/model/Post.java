package com.example.sk_blog.model;

import com.example.sk_blog.model.enums.ModerationStatus;
import com.example.sk_blog.service.PostService;
import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Set;


@Entity
@Table(name = "posts")
@JsonPropertyOrder({ "id", "timestamp", "user", "title", "announce", "likeCount", "dislikeCount", "commentCount", "viewCount" })
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "is_active", columnDefinition = "tinyint")
    @JsonIgnore
    private Integer isActive;

    @Column(name = "moderation_status", columnDefinition = "varchar(50) default 'NEW'")
    @NotNull
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
    ////////////////////////// json additional properties

    @Transient
    private int likeCount;
    @Transient
    private int dislikeCount;
    @Transient
    private int commentCount;
    @Transient
    private String announce;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Integer isActive() {
        return isActive;
    }

    public void setActive(Integer active) {
        isActive = active;
    }

    public ModerationStatus getModerationStatus() {
        return moderationStatus;
    }

    public void setModerationStatus(ModerationStatus moderationStatus) {
        this.moderationStatus = moderationStatus;
    }

    public Integer getModeratorId() {
        return moderatorId;
    }

    public void setModeratorId(Integer moderatorId) {
        this.moderatorId = moderatorId;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getViewCount() {
        return viewCount;
    }

    public void setViewCount(int viewCount) {
        this.viewCount = viewCount;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Set<PostVote> getPostVotes() {
        return postVotes;
    }

    public void setPostVotes(Set<PostVote> postVotes) {
        this.postVotes = postVotes;
    }

    public Set<PostComment> getPostComments() {
        return postComments;
    }

    public void setPostComments(Set<PostComment> postComments) {
        this.postComments = postComments;
    }

    public Set<Tag> getTags() {
        return tags;
    }

    public void setTags(Set<Tag> tags) {
        this.tags = tags;
    }

    public Integer getIsActive() {
        return isActive;
    }

    public void setIsActive(Integer isActive) {
        this.isActive = isActive;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public int getDislikeCount() {
        return dislikeCount;
    }

    public void setDislikeCount(int dislikeCount) {
        this.dislikeCount = dislikeCount;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    public String getAnnounce() {
        return announce;
    }

    public void setAnnounce(String announce) {
        this.announce = announce;
    }
}
