package com.example.sk_blog.config;

import com.example.sk_blog.dto.PostDTO;
import com.example.sk_blog.dto.UserDTO;
import com.example.sk_blog.model.Post;
import com.example.sk_blog.model.PostComment;
import com.example.sk_blog.model.PostVote;
import com.example.sk_blog.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring")
public interface PostDTOMapper {

    List<PostDTO> postsToDTOs(List<Post> posts);

    @Mapping(source = "time", target = "timestamp", qualifiedByName = "timeToStamp")
    @Mapping(source = "user", target = "user", qualifiedByName = "userToUserDTO")
    @Mapping(source = "text", target = "announce", qualifiedByName = "textToAnnounce")
    @Mapping(source = "postVotes", target = "likeCount", qualifiedByName = "likeCount")
    @Mapping(source = "postVotes", target = "dislikeCount", qualifiedByName = "dislikeCount")
    @Mapping(source = "postComments", target = "commentCount", qualifiedByName = "commentCount")
    PostDTO postToDTO(Post post);

    @Named("timeToStamp")
    static Long localDateTimeToTimestamp(LocalDateTime time) {
        return time.toEpochSecond(ZoneOffset.UTC);
    }

    @Named("userToUserDTO")
    @Mapping(target = "photo", ignore = true)
    UserDTO userToUserDTO(User user);

    @Named("textToAnnounce")
    static String textToAnnounce(String text) {
        return text.length() <= 150 ? text : text.substring(0 , 150).concat("...");
    }

    @Named("likeCount")
    static Integer likeCount(Set<PostVote> postVotes) {
        return Math.toIntExact(postVotes.stream().filter(x -> x.getValue() == 1).count());
    }

    @Named("dislikeCount")
    static Integer dislikeCount(Set<PostVote> postVotes) {
        return Math.toIntExact(postVotes.stream().filter(x -> x.getValue() == -1).count());
    }

    @Named("commentCount")
    static Integer commentCount(Set<PostComment> postComments) {
        return postComments.size();
    }
}
