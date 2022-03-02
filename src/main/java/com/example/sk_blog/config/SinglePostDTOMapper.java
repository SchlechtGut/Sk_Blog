package com.example.sk_blog.config;

import com.example.sk_blog.dto.CommentDTO;
import com.example.sk_blog.dto.SinglePostDTO;
import com.example.sk_blog.dto.UserDTO;
import com.example.sk_blog.model.Post;
import com.example.sk_blog.model.PostComment;
import com.example.sk_blog.model.Tag;
import com.example.sk_blog.model.User;
import org.mapstruct.Mapper;

import java.time.ZoneOffset;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;


@Mapper(componentModel = "spring")
public abstract class SinglePostDTOMapper {

    public SinglePostDTO toSinglePostDTO(Post post) {
        SinglePostDTO singlePostDTO = new SinglePostDTO();
        singlePostDTO.setId(post.getId());
        singlePostDTO.setTimestamp(post.getTime().toEpochSecond(ZoneOffset.UTC));
        singlePostDTO.setActive(post.getIsActive() == 1);
        singlePostDTO.setUser(post.getUser());
        singlePostDTO.setTitle(post.getTitle());
        singlePostDTO.setText(post.getText());
        singlePostDTO.setLikeCount(Math.toIntExact(post.getPostVotes().stream().filter(x -> x.getValue() == 1).count()));
        singlePostDTO.setDislikeCount(Math.toIntExact(post.getPostVotes().stream().filter(x -> x.getValue() == -1).count()));
        singlePostDTO.setViewCount(post.getViewCount());
        singlePostDTO.setComments(toCommentDTOs(post.getPostComments()));
        singlePostDTO.setTags(post.getTags().stream().map(Tag::getName).collect(Collectors.toSet()));

        return singlePostDTO;
    }

    private Set<CommentDTO> toCommentDTOs(Set<PostComment> postComments) {
        Set<CommentDTO> set = new HashSet<>();
        for (PostComment postComment : postComments) {
            CommentDTO commentDTO = new CommentDTO();
            commentDTO.setTimestamp(postComment.getTime().toEpochSecond(ZoneOffset.UTC));
            commentDTO.setId(postComment.getId());
            commentDTO.setText(postComment.getText());
            User commentUser = postComment.getUser();
            UserDTO commentUserDto = new UserDTO(commentUser.getId(), commentUser.getName(), commentUser.getPhoto());
            commentDTO.setUserDTO(commentUserDto);
            set.add(commentDTO);
        }
        return set;
    }
}
