package com.example.sk_blog.dto;

import lombok.Data;

import java.util.Set;

@Data
public class SinglePostDTO {

    private Integer id;
    private long timestamp;
    private Boolean active;
    private UserDTO user;
    private String title;
    private String text;
    private Integer likeCount;
    private Integer dislikeCount;
    private Integer viewCount;
    private Set<CommentDTO> comments;
    private Set<String> tags;
}
