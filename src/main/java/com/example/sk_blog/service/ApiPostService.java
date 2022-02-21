package com.example.sk_blog.service;

import com.example.sk_blog.api.response.InitResponse;
import com.example.sk_blog.api.response.PostResponse;
import com.example.sk_blog.dto.CommentDTO;
import com.example.sk_blog.dto.SinglePostDTO;
import com.example.sk_blog.dto.UserDTO;
import com.example.sk_blog.model.*;
import com.example.sk_blog.model.enums.ModerationStatus;
import com.example.sk_blog.repositories.CaptchaCodeRepository;
import com.example.sk_blog.repositories.GlobalSettingsRepository;
import com.example.sk_blog.repositories.PostRepository;
import com.example.sk_blog.repositories.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;

@Service
public class ApiPostService {

    private final PostRepository postRepository;

    @Autowired
    public ApiPostService(GlobalSettingsRepository globalSettingsRepository, PostRepository postRepository, InitResponse initResponse, TagRepository tagRepository, CaptchaCodeRepository captchaCodeRepository) {
        this.postRepository = postRepository;
    }

    public PostResponse getPosts(Integer offset, Integer limit, String mode) {
        Pageable nextPage = null;

        switch (mode) {
            case "popular": nextPage = PageRequest.of(offset, limit, Sort.by(Sort.Direction.DESC, "postComments"));
                break;
            case "best": nextPage = PageRequest.of(offset, limit, Sort.by(Sort.Direction.DESC, "postVotes"));
                break;
            case "early": nextPage = PageRequest.of(offset, limit, Sort.by(Sort.Direction.ASC, "time"));
                break;
            default: nextPage = PageRequest.of(offset, limit, Sort.by(Sort.Direction.DESC, "time"));
        }

        Page<Post> page = postRepository.findPostByIsActiveEqualsAndModerationStatusEqualsAndTimeBefore(1, ModerationStatus.ACCEPTED, LocalDateTime.now(), nextPage);
        List<Post> posts = page.getContent();
        setPostJsonFields(posts);

        return new PostResponse((int) page.getTotalElements(), posts);
    }

    public PostResponse getSearchedPosts(Integer offset, Integer limit, String query) {
        if (query == null || query.isBlank()) {
            return getPosts(offset, limit, "recent");
        }

        Pageable nextPage = PageRequest.of(offset, limit);
        Page<Post> page = postRepository.findPostByTitleContainingIgnoreCaseAndIsActiveAndModerationStatusAndTimeBefore(query,
                1, ModerationStatus.ACCEPTED, LocalDateTime.now(), nextPage);

        List<Post> posts = page.getContent();
        setPostJsonFields(posts);

        return new PostResponse((int) page.getTotalElements(), posts);
    }

    public PostResponse getPostsByDate(Integer offset, Integer limit, String date) {
        LocalDate searchedDate;

        if (date == null || date.isBlank()) {
            searchedDate = LocalDate.now();
        } else {
            searchedDate = LocalDate.parse(date);
        }

        Pageable nextPage = PageRequest.of(offset, limit);

        Page<Post> page = postRepository.findActiveAcceptedPostsByDate(searchedDate, LocalDateTime.now(), nextPage);
        List<Post> posts = page.getContent();

        setPostJsonFields(posts);

        return new PostResponse((int) page.getTotalElements(), posts);

    }

    public PostResponse getPostsByTag(Integer offset, Integer limit, String tag) {
        Pageable nextPage = PageRequest.of(offset, limit);

        Page<Post> page = postRepository.findByTagsAndModerationStatusAndIsActiveAndTimeBefore(tag, LocalDateTime.now(), nextPage);
        List<Post> posts = page.getContent();

        setPostJsonFields(posts);

        return new PostResponse((int) page.getTotalElements(), posts);
    }

    public ResponseEntity<SinglePostDTO> getPostById(Integer id) {

        Optional<Post> optional = postRepository.findById(id);
        Post post;
        if (optional.isPresent()) {
            post = optional.get();
            setPostJsonFields(Collections.singletonList(post));

            convertToSinglePostDto(post);


            return ResponseEntity.ok(convertToSinglePostDto(post));
        } else {
            return ResponseEntity.notFound().build();
        }

    }

    /////////////////////////private///////////////////////////////////////////////////

    private void setPostJsonFields(List<Post> posts) {
        for (Post post : posts) {
            post.setCommentCount(post.getPostComments().size());

            String announce;

            if (post.getText().length() > 150) {
                announce = post.getText().substring(0, 150);
                announce = announce.concat("...");
            } else {
                announce = post.getText();
            }

            post.setAnnounce(announce);

            int likeCount = 0;
            int dislikeCount = 0;

            for (PostVote postVote  : post.getPostVotes()) {
                if (postVote.getValue() == 1) {
                    likeCount++;
                } else {
                    dislikeCount++;
                }
            }

            post.setLikeCount(likeCount);
            post.setDislikeCount(dislikeCount);
        }
    }

    private SinglePostDTO convertToSinglePostDto(Post post) {
        SinglePostDTO singlePostDTO = new SinglePostDTO();
        singlePostDTO.setId(post.getId());
        singlePostDTO.setTimestamp(post.getTime().toEpochSecond(ZoneOffset.ofHours(3)));
        singlePostDTO.setActive(post.getIsActive() == 1);
        singlePostDTO.setUser(post.getUser());
        singlePostDTO.setTitle(post.getTitle());
        singlePostDTO.setText(post.getText());
        singlePostDTO.setLikeCount(post.getLikeCount());
        singlePostDTO.setDislikeCount(post.getDislikeCount());
        singlePostDTO.setViewCount(post.getViewCount());

        Set<CommentDTO> commentSet = new HashSet<>();
        for (PostComment comment : post.getPostComments()) {
            CommentDTO commentDTO = new CommentDTO();
            commentDTO.setId(comment.getId());
            commentDTO.setText(comment.getText());
            commentDTO.setTimestamp(comment.getTime().toEpochSecond(ZoneOffset.ofHours(3)));

            User user = comment.getUser();
            UserDTO userDTO = new UserDTO();
            userDTO.setId(user.getId());
            userDTO.setName(user.getName());
            userDTO.setPhoto(user.getPhoto());

            commentDTO.setUserDTO(userDTO);
            commentSet.add(commentDTO);
        }

        Set<String> tagSet = new HashSet<>();
        for (Tag tag : post.getTags()) {
            tagSet.add(tag.getName());
        }

        singlePostDTO.setComments(commentSet);
        singlePostDTO.setTags(tagSet);

        return singlePostDTO;
    }


}
