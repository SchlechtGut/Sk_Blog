package com.example.sk_blog.service;

import com.example.sk_blog.api.request.ModerationRequest;
import com.example.sk_blog.api.request.PostCommentRequest;
import com.example.sk_blog.api.request.PostIdRequest;
import com.example.sk_blog.api.request.PostRequest;
import com.example.sk_blog.api.response.ErrorMessageResponse;
import com.example.sk_blog.api.response.TrueOrErrorsResponse;
import com.example.sk_blog.dto.ListSizeDTO;
import com.example.sk_blog.model.*;
import com.example.sk_blog.model.enums.ModerationStatus;
import com.example.sk_blog.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ApiPostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final TagRepository tagRepository;
    private final PostCommentRepository postCommentRepository;
    private final PostVoteRepository postVoteRepository;

    @Autowired
    public ApiPostService(PostRepository postRepository, UserRepository userRepository, TagRepository tagRepository, PostCommentRepository postCommentRepository, PostVoteRepository postVoteRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.tagRepository = tagRepository;
        this.postCommentRepository = postCommentRepository;
        this.postVoteRepository = postVoteRepository;
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

    public ListSizeDTO getPostsByPostStatus(Integer offset, Integer limit, String status) {
        Page<Post> page = null;
        Pageable nextPage = PageRequest.of(offset, limit, Sort.by(Sort.Direction.DESC, "time"));

        User user = getCurrentUser();

        switch (status) {
            case "inactive": page = postRepository.findByUserAndIsActive(user, 0, nextPage);
                break;
            case "pending": page = postRepository.findByUserAndIsActiveAndModerationStatus(user, 1, ModerationStatus.NEW, nextPage);
                break;
            case "declined": page = postRepository.findByUserAndIsActiveAndModerationStatus(user, 1, ModerationStatus.DECLINED, nextPage);
                break;
            case "published": page = postRepository.findByUserAndIsActiveAndModerationStatus(user, 1, ModerationStatus.ACCEPTED, nextPage);
        }

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

    public ListSizeDTO getPostsByModerationStatus(Integer offset, Integer limit, String status) {
        Page<Post> page = null;
        Pageable nextPage = PageRequest.of(offset, limit, Sort.by(Sort.Direction.DESC, "time"));

        User currentUser = getCurrentUser();

        switch (status) {
            case "new": page = postRepository.findByModerationStatusAndIsActive(ModerationStatus.NEW, 1, nextPage);
                break;
            case "declined": page = postRepository.findByModerationStatusAndModeratorIdAndIsActive(ModerationStatus.DECLINED, currentUser.getId(), 1, nextPage);
                break;
            case "accepted": page = postRepository.findByModerationStatusAndModeratorIdAndIsActive(ModerationStatus.ACCEPTED, currentUser.getId(), 1, nextPage);
        }

        assert page != null;
        List<Post> posts = page.getContent();

        return new ListSizeDTO((int) page.getTotalElements(), posts);
    }



    public TrueOrErrorsResponse addPost(PostRequest request, BindingResult bindingResult, Authentication authentication) {
        return saveOrUpdatePost(Optional.empty(), request, bindingResult, authentication);
    }

    public TrueOrErrorsResponse editPost(Integer postId, PostRequest request, BindingResult bindingResult, Authentication authentication) {
        return saveOrUpdatePost(Optional.of(postId), request, bindingResult, authentication);
    }

    public ResponseEntity<?> addPostComment(PostCommentRequest postCommentRequest, BindingResult bindingResult) {
        Integer postId = postCommentRequest.getPostId();
        Integer parentId = postCommentRequest.getParentId();
        String text = postCommentRequest.getText();

        if (postId != null && !postRepository.existsById(postId)) {
            return ResponseEntity.badRequest().body(new ErrorMessageResponse("post doesn't exist"));
        } else if (parentId != null && !postRepository.existsById(parentId)) {
            return ResponseEntity.badRequest().body(new ErrorMessageResponse("comment that's being commented doesn't exist"));
        }

        Map<String, String> errors = new LinkedHashMap<>();

        if (bindingResult.hasErrors()) {
            for (ObjectError error : bindingResult.getAllErrors()) {
                String fieldName = ((FieldError) error).getField();
                String errorMessage = error.getDefaultMessage();
                errors.put(fieldName, errorMessage);
            }
        }

        if (errors.isEmpty()) {
            PostComment postComment = new PostComment();

            if (parentId != null) {
                postComment.setParentId(parentId);
            }
            postComment.setTime(LocalDateTime.now());
            postComment.setText(text);
            postComment.setUser(getCurrentUser());
            postComment.setPost(postRepository.findById(postId).get());

            int id = postCommentRepository.save(postComment).getId();

            return ResponseEntity.ok(new IdResponse(id));

        } else {
            return ResponseEntity.ok(new TrueOrErrorsResponse(errors));
        }
    }


    public TrueOrErrorsResponse moderatePost(ModerationRequest moderationRequest) {
        String decline = "decline";
        String accept = "accept";

        Integer postId = moderationRequest.getPostId();
        String decision = moderationRequest.getDecision();

        Optional<Post> optional = postRepository.findById(postId);

        if (optional.isEmpty() || !decision.equals(decline) && !decision.equals(accept)) {
            return new TrueOrErrorsResponse();
        } else {
            Post post = optional.get();
            post.setModerationStatus(decision.equals(accept) ? ModerationStatus.ACCEPTED : ModerationStatus.DECLINED);
            post.setModeratorId(getCurrentUser().getId());

            postRepository.save(post);
            return new TrueOrErrorsResponse(true);
        }
    }

    public TrueOrErrorsResponse votePost(PostIdRequest postIdRequest, Integer value) {
        Integer postId = postIdRequest.getPostId();
        User user = getCurrentUser();
        Optional<Post> optional = postRepository.findById(postId);

        if (optional.isPresent()) {
            Post post = optional.get();

            if (alreadyHasThatVote(post, value)) {
                return new TrueOrErrorsResponse();
            } else if (alreadyHasOppositeVote(post, value)) {
                PostVote existingOpposite = postVoteRepository.findByValueAndPostAndUser(-value, post, user);
                existingOpposite.setValue(value);
                postVoteRepository.save(existingOpposite);
                return new TrueOrErrorsResponse(true);
            }

            PostVote postVote = new PostVote();
            postVote.setPost(post);
            postVote.setUser(user);
            postVote.setTime(LocalDateTime.now());
            postVote.setValue(value);

            postVoteRepository.save(postVote);
            return new TrueOrErrorsResponse(true);
        }

        return new TrueOrErrorsResponse();
    }

    ////////////////////private////////////////////////////////////////////////////////////////////////////////////////////////

    private TrueOrErrorsResponse saveOrUpdatePost(Optional<Integer> postId , PostRequest request, BindingResult bindingResult, Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User currentUser = userRepository.findByEmail(userDetails.getUsername()).get();

        boolean editOperation = postId.isPresent();

        Post originalPost = null;
        Set<String> originalPostTags = null;

        Map<String, String> errors = new LinkedHashMap<>();

        if (bindingResult.hasErrors()) {
            for (ObjectError error : bindingResult.getAllErrors()) {
                String fieldName = ((FieldError) error).getField();
                String errorMessage = error.getDefaultMessage();
                errors.put(fieldName, errorMessage);
            }
        }

        if (editOperation) {
            originalPost = postRepository.getById(postId.get());
            originalPostTags = originalPost.getTags().stream().map(Tag::getName).collect(Collectors.toSet());
            if (!originalPost.getUser().getId().equals(currentUser.getId()) && !currentUser.isModerator()) {
                errors.put("rights", "neither author nor moderator");
            }
        }

        Post finalOriginalPost = originalPost;

        if (errors.isEmpty()) {
            if (new Date().getTime() > request.getTimestamp()) {
                request.setTimestamp(new Date().getTime());
            }

            Set<Tag> tags = new HashSet<>();

            for (String tagName : request.getTags()) {
                Tag tag = tagRepository.findByNameIgnoreCase(tagName);

                if (tag != null) {
                    tags.add(tag);
                } else {
                    Tag savedTag = tagRepository.save(new Tag(tagName));
                    tags.add(savedTag);
                }
            }

            Post post = new Post();
            post.setIsActive(request.getActive());
            post.setTitle(request.getTitle());
            post.setText(request.getText());
            post.setModerationStatus(ModerationStatus.NEW);
            post.setTags(tags);
            postId.ifPresentOrElse(x -> post.setUser(finalOriginalPost.getUser()), () -> post.setUser(currentUser));
            post.setTime(Instant.ofEpochMilli(request.getTimestamp()).atZone(ZoneId.systemDefault()).toLocalDateTime());

            postId.ifPresent(post::setId);
            postId.ifPresent(x -> {
                if (!post.getUser().getId().equals(currentUser.getId())) {
                    post.setModerationStatus(finalOriginalPost.getModerationStatus());
                }
            });

            Post savedPost = postRepository.save(post);

            for (Tag tag : tags) {
                if (tag.getPosts() == null) {
                    Set<Post> posts = new HashSet<>();
                    tag.setPosts(posts);
                }
                tag.getPosts().add(savedPost);
                tagRepository.save(tag);
            }

            if (editOperation) {
                Set<String> newTags = post.getTags().stream().map(Tag::getName).collect(Collectors.toSet());
                Set<String> deletedTags = originalPostTags.stream().filter(e -> !newTags.contains(e)).collect(Collectors.toSet());

                deletedTags.forEach(System.out::println);

                for (String tag : deletedTags) {  //
                    Tag deletedTag = tagRepository.findByNameIgnoreCase(tag);
                    deletedTag.getPosts().removeIf(x -> x.getId() == post.getId());
                    tagRepository.save(deletedTag);
                }
            }

            return new TrueOrErrorsResponse(true);
        } else {
            return new TrueOrErrorsResponse(errors);
        }
    }

    private User getCurrentUser() {
        org.springframework.security.core.userdetails.User securityUser =
                (org.springframework.security.core.userdetails.User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userRepository.findByEmail(securityUser.getUsername()).get();
    }

    private boolean alreadyHasThatVote(Post post, Integer value) {
        return postVoteRepository.existsByValueAndPostAndUser(value, post, getCurrentUser());
    }

    private boolean alreadyHasOppositeVote(Post post, Integer value) {
        return postVoteRepository.existsByValueAndPostAndUser(-value, post, getCurrentUser());
    }



    static class IdResponse {
        Integer id;

        public IdResponse(Integer id) {
            this.id = id;
        }

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }
    }





}
