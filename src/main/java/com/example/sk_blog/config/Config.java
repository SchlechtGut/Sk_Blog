package com.example.sk_blog.config;

import com.example.sk_blog.dto.CommentDTO;
import com.example.sk_blog.dto.PostDTO;
import com.example.sk_blog.dto.SinglePostDTO;
import com.example.sk_blog.dto.UserDTO;
import com.example.sk_blog.model.*;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Configuration
public class Config {

    @Bean
    public ModelMapper singlePostDTOModelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        TypeMap<Post, SinglePostDTO> propertyMapper = modelMapper.createTypeMap(Post.class, SinglePostDTO.class);

        Converter<LocalDateTime, Long> localDateTimeToEpoch = c -> c.getSource().toEpochSecond(ZoneOffset.UTC);
        Converter<Set<PostVote>, Integer> postVotesTOLikesCount = (c) -> Math.toIntExact(c.getSource().stream().filter(x -> x.getValue() == 1).count());
        Converter<Set<PostVote>, Integer> postVotesTODislikesCount = (c) -> Math.toIntExact(c.getSource().stream().filter(x -> x.getValue() == -1).count());
        Converter<Set<Tag>, Set<String>> tagsToNames = (c) -> c.getSource().stream().map(Tag::getName).collect(Collectors.toSet());

        Converter<Set<PostComment>, Set<CommentDTO>> postsToDTOs = (c) -> {
            Set<CommentDTO> set = new HashSet<>();
            for (PostComment postComment : c.getSource()) {
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
        };

        propertyMapper.addMappings(mapper -> mapper.using(localDateTimeToEpoch).map(Post::getTime, SinglePostDTO::setTimestamp));
        propertyMapper.addMappings(mapper -> mapper.using(postsToDTOs).map(Post::getPostComments, SinglePostDTO::setComments));
        propertyMapper.addMappings(mapper -> mapper.using(postVotesTOLikesCount).map(Post::getPostVotes, SinglePostDTO::setLikeCount));
        propertyMapper.addMappings(mapper -> mapper.using(postVotesTODislikesCount).map(Post::getPostVotes, SinglePostDTO::setDislikeCount));
        propertyMapper.addMappings(mapper -> mapper.using(tagsToNames).map(Post::getTags, SinglePostDTO::setTags));

        return modelMapper;
    }

    @Bean
    public ModelMapper postDTOModelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        TypeMap<Post, PostDTO> propertyMapper = modelMapper.createTypeMap(Post.class, PostDTO.class);

        Converter<LocalDateTime, Long> localDateTimeToEpoch = c -> c.getSource().toEpochSecond(ZoneOffset.UTC);
        Converter<String, String> textToAnnounce = (c) -> c.getSource().length() <= 150 ? c.getSource() : c.getSource().substring(0 , 150).concat("...");
        Converter<Set<PostVote>, Integer> postVotesTOLikesCount = (c) -> Math.toIntExact(c.getSource().stream().filter(x -> x.getValue() == 1).count());
        Converter<Set<PostVote>, Integer> postVotesTODislikesCount = (c) -> Math.toIntExact(c.getSource().stream().filter(x -> x.getValue() == -1).count());
        Converter<Set<PostComment>, Integer> commentsToCount = (c) -> c.getSource().size();

        propertyMapper.addMappings(mapper -> mapper.using(localDateTimeToEpoch).map(Post::getTime, PostDTO::setTimestamp));
        propertyMapper.addMappings(mapper -> mapper.using(textToAnnounce).map(Post::getText, PostDTO::setAnnounce));
        propertyMapper.addMappings(mapper -> mapper.using(postVotesTOLikesCount).map(Post::getPostVotes, PostDTO::setLikeCount));
        propertyMapper.addMappings(mapper -> mapper.using(postVotesTODislikesCount).map(Post::getPostVotes, PostDTO::setDislikeCount));
        propertyMapper.addMappings(mapper -> mapper.using(commentsToCount).map(Post::getPostComments, PostDTO::setCommentCount));

        return modelMapper;
    }
}
