package com.example.sk_blog.controller;

import com.example.sk_blog.api.request.PostIdRequest;
import com.example.sk_blog.api.request.PostRequest;
import com.example.sk_blog.api.response.TrueOrErrorsResponse;
import com.example.sk_blog.api.response.PostResponse;
import com.example.sk_blog.config.PostDTOMapper;
import com.example.sk_blog.config.SinglePostDtoMapper;
import com.example.sk_blog.dto.ListSizeDTO;
import com.example.sk_blog.dto.SinglePostDTO;
import com.example.sk_blog.model.*;
import com.example.sk_blog.service.ApiPostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/post")
public class ApiPostController {

    private final ApiPostService apiPostService;
    private final SinglePostDtoMapper singlePostDtoMapper;
    private final PostDTOMapper postDTOMapper;

    @Autowired
    public ApiPostController(ApiPostService apiPostService, SinglePostDtoMapper singlePostDtoMapper, PostDTOMapper postDTOMapper) {
        this.apiPostService = apiPostService;
        this.singlePostDtoMapper = singlePostDtoMapper;
        this.postDTOMapper = postDTOMapper;
    }

    @GetMapping
    public PostResponse posts(@RequestParam(defaultValue = "0") Integer offset,
                              @RequestParam(defaultValue = "20") Integer limit,
                              @RequestParam(required = false, defaultValue = "recent") String mode) {
        ListSizeDTO listSizeDTO = apiPostService.getPosts(offset, limit, mode);
        return new PostResponse(listSizeDTO.getSize(), postDTOMapper.postsToDTOs(listSizeDTO.getList()));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('user:write')")
    public TrueOrErrorsResponse addPost(@RequestBody @Valid PostRequest request, BindingResult bindingResult, Authentication authentication) {
        return apiPostService.addPost(request, bindingResult, authentication);
    }


    @PutMapping("/{id}")
    public TrueOrErrorsResponse editPost(@PathVariable Integer id, @RequestBody @Valid PostRequest request, BindingResult bindingResult, Authentication authentication) {
        return apiPostService.editPost(id, request, bindingResult, authentication);
    }

    @GetMapping("/my")
    @PreAuthorize("hasAuthority('user:write')")
    public PostResponse myPosts(@RequestParam(defaultValue = "0") Integer offset,
                                @RequestParam(defaultValue = "20") Integer limit,
                                @RequestParam String status) {
        ListSizeDTO listSizeDTO = apiPostService.getPostsByPostStatus(offset, limit, status);

        return new PostResponse(listSizeDTO.getSize(), postDTOMapper.postsToDTOs(listSizeDTO.getList()));
    }

    @GetMapping("/moderation")
    @PreAuthorize("hasAuthority('user:moderate')")
    public PostResponse postsForModeration(@RequestParam(defaultValue = "0") Integer offset,
                                @RequestParam(defaultValue = "20") Integer limit,
                                @RequestParam String status) {
        ListSizeDTO listSizeDTO = apiPostService.getPostsByModerationStatus(offset, limit, status);

        return new PostResponse(listSizeDTO.getSize(), postDTOMapper.postsToDTOs(listSizeDTO.getList()));
    }

    @GetMapping("/search")
    public PostResponse searchedPosts(@RequestParam(defaultValue = "0") Integer offset, @RequestParam(defaultValue = "20") Integer limit, @RequestParam String query) {
        ListSizeDTO listSizeDTO = apiPostService.getSearchedPosts(offset, limit, query);
        return new PostResponse(listSizeDTO.getSize(), postDTOMapper.postsToDTOs(listSizeDTO.getList()));
    }

    @GetMapping("/byDate")
    public PostResponse postsByDate(@RequestParam Integer offset, @RequestParam Integer limit, @RequestParam(required = false) String date) {
        ListSizeDTO listSizeDTO = apiPostService.getPostsByDate(offset, limit, date);
        return new PostResponse(listSizeDTO.getSize(), postDTOMapper.postsToDTOs(listSizeDTO.getList()));
    }

    @GetMapping("/byTag")
    public PostResponse postsByTag(@RequestParam Integer offset, @RequestParam Integer limit, @RequestParam String tag) {
        ListSizeDTO listSizeDTO = apiPostService.getPostsByTag(offset, limit, tag);
        return new PostResponse(listSizeDTO.getSize(), postDTOMapper.postsToDTOs(listSizeDTO.getList()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SinglePostDTO> postById(@PathVariable Integer id) {
        Post post = apiPostService.getPostById(id);
        if (post != null) {
            return ResponseEntity.ok(singlePostDtoMapper.singlePostToDTO(post));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/like")
    @PreAuthorize("hasAuthority('user:write')")
    public TrueOrErrorsResponse likePost(@RequestBody PostIdRequest postIdRequest) {
        return apiPostService.votePost(postIdRequest, 1);
    }

    @PostMapping("/dislike")
    @PreAuthorize("hasAuthority('user:write')")
    public TrueOrErrorsResponse dislikePost(@RequestBody PostIdRequest postIdRequest) {
        return apiPostService.votePost(postIdRequest, -1);
    }

/////////////////////////////private/////////////////////////////////////////////////////////////////////////////

}
