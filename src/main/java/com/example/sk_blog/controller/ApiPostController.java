package com.example.sk_blog.controller;

import com.example.sk_blog.api.response.PostResponse;
import com.example.sk_blog.dto.PostDTO;
import com.example.sk_blog.dto.SinglePostDTO;
import com.example.sk_blog.model.*;
import com.example.sk_blog.service.ApiPostService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/post")
public class ApiPostController {

    private final ApiPostService apiPostService;
    private final ModelMapper singlePostDTOModelMapper;
    private final ModelMapper postDTOModelMapper;

    @Autowired
    public ApiPostController(ApiPostService apiPostService, ModelMapper singlePostDTOModelMapper, ModelMapper postDTOModelMapper) {
        this.apiPostService = apiPostService;
        this.singlePostDTOModelMapper = singlePostDTOModelMapper;
        this.postDTOModelMapper = postDTOModelMapper;
    }

    @GetMapping
    public PostResponse posts(@RequestParam Integer offset, @RequestParam Integer limit, @RequestParam(required = false, defaultValue = "recent") String mode) {
        Pair<Integer, List<Post>> pair = apiPostService.getPosts(offset, limit, mode);
        return new PostResponse(pair.getFirst(), convertPostsToPostDTOs(pair.getSecond()));
    }

    @GetMapping("/search")
    public PostResponse searchedPosts(@RequestParam Integer offset, @RequestParam Integer limit, @RequestParam String query) {
        Pair<Integer, List<Post>> pair = apiPostService.getSearchedPosts(offset, limit, query);
        return new PostResponse(pair.getFirst(), convertPostsToPostDTOs(pair.getSecond()));
    }

    @GetMapping("/byDate")
    public PostResponse postsByDate(@RequestParam Integer offset, @RequestParam Integer limit, @RequestParam(required = false) String date) {
        Pair<Integer, List<Post>> pair = apiPostService.getPostsByDate(offset, limit, date);
        return new PostResponse(pair.getFirst(), convertPostsToPostDTOs(pair.getSecond()));
    }

    @GetMapping("/byTag")
    public PostResponse postsByTag(@RequestParam Integer offset, @RequestParam Integer limit, @RequestParam String tag) {
        Pair<Integer, List<Post>> pair = apiPostService.getPostsByTag(offset, limit, tag);
        return new PostResponse(pair.getFirst(), convertPostsToPostDTOs(pair.getSecond()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SinglePostDTO> postById(@PathVariable Integer id) {
        Post post = apiPostService.getPostById(id);
        if (post != null) {
            return ResponseEntity.ok(convertToSinglePostDTO(post));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

/////////////////////////////private/////////////////////////////////////////////////////////////////////////////

    private List<PostDTO> convertPostsToPostDTOs(List<Post> posts) {
        return posts.stream().map(post -> postDTOModelMapper.map(post, PostDTO.class)).collect(Collectors.toList());
    }

    private SinglePostDTO convertToSinglePostDTO(Post post) {
        return singlePostDTOModelMapper.map(post, SinglePostDTO.class);
    }
}
