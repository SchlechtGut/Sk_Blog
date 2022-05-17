package com.example.sk_blog.controller;

import com.example.sk_blog.api.response.PostResponse;
import com.example.sk_blog.config.PostDTOMapper;
import com.example.sk_blog.config.SinglePostDtoMapper;
import com.example.sk_blog.dto.ListSizeDTO;
import com.example.sk_blog.dto.PostDTO;
import com.example.sk_blog.dto.SinglePostDTO;
import com.example.sk_blog.model.*;
import com.example.sk_blog.service.ApiPostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

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

    @GetMapping("/my")
    @PreAuthorize("hasAuthority('user:write')")
    public PostResponse myPosts(@RequestParam(defaultValue = "0") Integer offset,
                                @RequestParam(defaultValue = "20") Integer limit,
                                @RequestParam String status) {
        System.out.println(SecurityContextHolder.getContext().getAuthentication().isAuthenticated());


        ListSizeDTO listSizeDTO = apiPostService.getPostsByStatus(offset, limit, status);

        return new PostResponse(listSizeDTO.getSize(), postDTOMapper.postsToDTOs(listSizeDTO.getList()));
    }

    @GetMapping("/search")
    @PreAuthorize("hasAuthority('user:moderate')")
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

/////////////////////////////private/////////////////////////////////////////////////////////////////////////////

}
