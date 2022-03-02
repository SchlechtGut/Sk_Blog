package com.example.sk_blog.config;

import com.example.sk_blog.dto.PostDTO;
import com.example.sk_blog.model.Post;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

import java.time.ZoneOffset;

@Mapper(componentModel = "spring")
public abstract class PostDTOMapper {

    public PostDTO toPostDTO(Post post) {
        PostDTO postDTO = new PostDTO();

        postDTO.setId(post.getId());
        postDTO.setTimestamp(post.getTime().toEpochSecond(ZoneOffset.UTC));
        postDTO.setUser(post.getUser());
        postDTO.setTitle(postDTO.getTitle());
        postDTO.setAnnounce(post.getText().length() <= 150 ? post.getText() : post.getText().substring(0 , 150).concat("..."));
        postDTO.setLikeCount(Math.toIntExact(post.getPostVotes().stream().filter(x -> x.getValue() == 1).count()));
        postDTO.setDislikeCount(Math.toIntExact(post.getPostVotes().stream().filter(x -> x.getValue() == -1).count()));
        postDTO.setCommentCount(post.getPostComments().size());
        postDTO.setViewCount(post.getViewCount());

        return postDTO;
    }
}
