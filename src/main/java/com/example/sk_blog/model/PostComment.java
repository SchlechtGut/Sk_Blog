package com.example.sk_blog.model;


import com.example.sk_blog.service.PostService;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "post_comments")
@Data
public class PostComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "parent_id")
    @JsonIgnore
    private Integer parentId;

    @Column
    @JsonProperty(value = "timestamp")
    @JsonSerialize(using = PostService.CustomDateSerializer.class)
    private LocalDateTime time;

    @Column(columnDefinition = "text")
    @NotNull
    private String text;

    @ManyToOne
    @JoinColumn(name="user_id", nullable=false)
    private User user;

    @ManyToOne
    @JoinColumn(name="post_id", nullable=false)
    @JsonIgnore
    private Post post;
}
