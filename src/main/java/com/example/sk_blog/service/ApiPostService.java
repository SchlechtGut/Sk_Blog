package com.example.sk_blog.service;

import com.example.sk_blog.dto.ListSizeDTO;
import com.example.sk_blog.model.Post;
import com.example.sk_blog.model.enums.ModerationStatus;
import com.example.sk_blog.repositories.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ApiPostService {

    private final PostRepository postRepository;

    @Autowired
    public ApiPostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public ListSizeDTO getPosts(Integer offset, Integer limit, String mode) {
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

        return new ListSizeDTO((int) page.getTotalElements(), posts);
    }

    public ListSizeDTO getSearchedPosts(Integer offset, Integer limit, String query) {
        if (query == null || query.isBlank()) {
            return getPosts(offset, limit, "recent");
        }

        Pageable nextPage = PageRequest.of(offset, limit);
        Page<Post> page = postRepository.findPostByTitleContainingIgnoreCaseAndIsActiveAndModerationStatusAndTimeBefore(query,
                1, ModerationStatus.ACCEPTED, LocalDateTime.now(), nextPage);

        List<Post> posts = page.getContent();

        return new ListSizeDTO((int) page.getTotalElements(), posts);
    }

    public ListSizeDTO getPostsByDate(Integer offset, Integer limit, String date) {
        LocalDate searchedDate;

        if (date == null || date.isBlank()) {
            searchedDate = LocalDate.now();
        } else {
            searchedDate = LocalDate.parse(date);
        }

        Pageable nextPage = PageRequest.of(offset, limit);

        Page<Post> page = postRepository.findActiveAcceptedPostsByDate(searchedDate, LocalDateTime.now(), nextPage);
        List<Post> posts = page.getContent();

        return new ListSizeDTO((int) page.getTotalElements(), posts);

    }

    public ListSizeDTO getPostsByTag(Integer offset, Integer limit, String tag) {
        Pageable nextPage = PageRequest.of(offset, limit);

        Page<Post> page = postRepository.findByTagsAndModerationStatusAndIsActiveAndTimeBefore(tag, LocalDateTime.now(), nextPage);
        List<Post> posts = page.getContent();

        return new ListSizeDTO((int) page.getTotalElements(), posts);
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

}
