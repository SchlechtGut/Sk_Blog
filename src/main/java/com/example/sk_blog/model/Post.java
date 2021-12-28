package com.example.sk_blog.model;

import com.example.sk_blog.model.enums.ModerationStatus;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.Set;

@Entity
@Table(name = "posts")
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "is_active", columnDefinition = "tinyint")
    @NotNull
    private boolean isActive;

    @Column(name = "moderation_status", columnDefinition = "varchar(50) default 'NEW'")
    @NotNull
    private ModerationStatus moderationStatus;

    @Column(name = "moderator_id")
    private int moderatorId;

    @NotNull
    private Date time;

    @NotNull
    private String title;

    @Column(columnDefinition = "text")
    @NotNull
    private String text;

    @Column(name = "view_count")
    @NotNull
    private int viewCount;

    @ManyToOne
    @JoinColumn(name="user_id", nullable=false)
    private User user;

    @OneToMany(mappedBy = "post")
    private Set<PostVote> postVotes;

    @OneToMany(mappedBy = "post")
    private Set<PostComment> postComments;

    @ManyToMany(mappedBy = "posts")
    private Set<Tag> tags;


}
