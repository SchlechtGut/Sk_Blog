package com.example.sk_blog.config;

import com.example.sk_blog.dto.CommentDTO;
import com.example.sk_blog.dto.SinglePostDTO;
import com.example.sk_blog.dto.UserDTO;
import com.example.sk_blog.model.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface SinglePostDtoMapper {

    @Mapping(source = "time", target = "timestamp", qualifiedByName = "timeToStamp")
    @Mapping(source = "isActive", target = "active", qualifiedByName = "isActive")
    @Mapping(source = "user", target = "user", qualifiedByName = "userToUserDTO")
    @Mapping(source = "postVotes", target = "likeCount", qualifiedByName = "likeCount")
    @Mapping(source = "postVotes", target = "dislikeCount", qualifiedByName = "dislikeCount")
    @Mapping(source = "postComments", target = "comments", qualifiedByName = "commentsToDTOs")
    @Mapping(source = "tags", target = "tags", qualifiedByName = "tagsToStrings")
    SinglePostDTO singlePostToDTO(Post post);

    @Named("timeToStamp")
    static Long localDateTimeToTimestamp(LocalDateTime time) {
        return time.toEpochSecond(ZoneOffset.UTC);
    }

    @Named("isActive")
    static Boolean active(Integer isActive) {
        return isActive == 1;
    }

    @Named("userToUserDTO")
    @Mapping(target = "photo", ignore = true)
    UserDTO userToUserDTO(User user);

    @Named("likeCount")
    static Integer likeCount(Set<PostVote> postVotes) {
        return Math.toIntExact(postVotes.stream().filter(x -> x.getValue() == 1).count());
    }

    @Named("dislikeCount")
    static Integer dislikeCount(Set<PostVote> postVotes) {
        return Math.toIntExact(postVotes.stream().filter(x -> x.getValue() == -1).count());
    }

    @Named("tagsToStrings")
    static Set<String> tagsToStrings(Set<Tag> tags) {
        return tags.stream().map(Tag::getName).collect(Collectors.toSet());
    }

    @Named("commentsToDTOs")
    Set<CommentDTO> commentsToDTOs(Set<PostComment> postComments);

    @Mapping(source = "time", target = "timestamp", qualifiedByName = "timeToStamp")
    @Mapping(source = "user", target = "user", qualifiedByName = "userToUserDTOWithPhoto")
    CommentDTO commentToDTO(PostComment postComment);

    @Named("userToUserDTOWithPhoto")
    UserDTO userToUserDTOWithPhoto(User user);


}
