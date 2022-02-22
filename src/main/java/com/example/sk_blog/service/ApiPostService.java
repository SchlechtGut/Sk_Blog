package com.example.sk_blog.service;

import com.example.sk_blog.api.response.PostResponse;
import com.example.sk_blog.dto.CommentDTO;
import com.example.sk_blog.dto.PostDTO;
import com.example.sk_blog.dto.SinglePostDTO;
import com.example.sk_blog.dto.UserDTO;
import com.example.sk_blog.model.*;
import com.example.sk_blog.model.enums.ModerationStatus;
import com.example.sk_blog.repositories.PostRepository;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.util.Pair;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ApiPostService {

    private final PostRepository postRepository;

    @Autowired
    public ApiPostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public Pair<Integer, List<Post>> getPosts(Integer offset, Integer limit, String mode) {
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

        return Pair.of((int) page.getTotalElements(), posts);
    }



    public Pair<Integer, List<Post>> getSearchedPosts(Integer offset, Integer limit, String query) {
        if (query == null || query.isBlank()) {
            return getPosts(offset, limit, "recent");
        }

        Pageable nextPage = PageRequest.of(offset, limit);
        Page<Post> page = postRepository.findPostByTitleContainingIgnoreCaseAndIsActiveAndModerationStatusAndTimeBefore(query,
                1, ModerationStatus.ACCEPTED, LocalDateTime.now(), nextPage);

        List<Post> posts = page.getContent();

        return Pair.of((int) page.getTotalElements(), posts);
    }

    public Pair<Integer, List<Post>> getPostsByDate(Integer offset, Integer limit, String date) {
        LocalDate searchedDate;

        if (date == null || date.isBlank()) {
            searchedDate = LocalDate.now();
        } else {
            searchedDate = LocalDate.parse(date);
        }

        Pageable nextPage = PageRequest.of(offset, limit);

        Page<Post> page = postRepository.findActiveAcceptedPostsByDate(searchedDate, LocalDateTime.now(), nextPage);
        List<Post> posts = page.getContent();

        return Pair.of((int) page.getTotalElements(), posts);

    }

    public Pair<Integer, List<Post>> getPostsByTag(Integer offset, Integer limit, String tag) {
        Pageable nextPage = PageRequest.of(offset, limit);

        Page<Post> page = postRepository.findByTagsAndModerationStatusAndIsActiveAndTimeBefore(tag, LocalDateTime.now(), nextPage);
        List<Post> posts = page.getContent();

        return Pair.of((int) page.getTotalElements(), posts);
    }

    public Post getPostById(Integer id) {
        Optional<Post> optional = postRepository.findById(id);
        Post post = null;
        if (optional.isPresent()) {
            post = optional.get();

        }
        return post;
    }



    /////////////////////////private///////////////////////////////////////////////////

//    private void setPostJsonFields(List<PostDTO> posts) {
//        for (PostDTO post : posts) {
//            post.setCommentCount(post.getPostComments().size());
//
//            String announce;
//
//            if (post.getText().length() > 150) {
//                announce = post.getText().substring(0, 150);
//                announce = announce.concat("...");
//            } else {
//                announce = post.getText();
//            }
//
//            post.setAnnounce(announce);
//
//            int likeCount = 0;
//            int dislikeCount = 0;
//
//            for (PostVote postVote  : post.getPostVotes()) {
//                if (postVote.getValue() == 1) {
//                    likeCount++;
//                } else {
//                    dislikeCount++;
//                }
//            }
//
//            post.setLikeCount(likeCount);
//            post.setDislikeCount(dislikeCount);
//        }
//    }


}
