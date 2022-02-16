package com.example.sk_blog.service;

import com.example.sk_blog.api.response.PostResponse;
import com.example.sk_blog.api.response.TagResponse;
import com.example.sk_blog.model.GlobalSetting;
import com.example.sk_blog.api.response.InitResponse;
import com.example.sk_blog.api.response.SettingsResponse;
import com.example.sk_blog.model.Post;
import com.example.sk_blog.model.PostVote;
import com.example.sk_blog.model.Tag;
import com.example.sk_blog.model.enums.ModerationStatus;
import com.example.sk_blog.repositories.GlobalSettingsRepository;
import com.example.sk_blog.repositories.PostRepository;
import com.example.sk_blog.repositories.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;

@Service
public class ApiService {
    private final GlobalSettingsRepository globalSettingsRepository;
    private final PostRepository postRepository;
    private final InitResponse initResponse;
    private final TagRepository tagRepository;

    @Autowired
    public ApiService(GlobalSettingsRepository globalSettingsRepository, PostRepository postRepository, InitResponse initResponse, TagRepository tagRepository) {
        this.globalSettingsRepository = globalSettingsRepository;
        this.postRepository = postRepository;
        this.initResponse = initResponse;
        this.tagRepository = tagRepository;
    }

    public InitResponse getInitResponse() {
        return initResponse;
    }

    public SettingsResponse getGlobalSettings() {
        String multi = null;
        String moder = null;
        String statistic = null;

        for(GlobalSetting setting : globalSettingsRepository.findAll()) {
            switch (setting.getCode()) {
                case "MULTIUSER_MODE": multi = setting.getValue();
                    break;
                case "POST_PREMODERATION": moder = setting.getValue();
                    break;
                case "STATISTICS_IS_PUBLIC": statistic = setting.getValue();
            }
        }

        List<String> list = new ArrayList<>(Arrays.asList(multi, moder, statistic));

        for (int i = 0;i < list.size();i++) {
            if (list.get(i).equals("YES")) {
                list.set(i, "true");
            } else {
                list.set(i, "false");
            }
        }

        return new SettingsResponse(Boolean.parseBoolean(list.get(0)), Boolean.parseBoolean(list.get(1)), Boolean.parseBoolean(list.get(2)));
    }



    public PostResponse getPosts(Integer offset, Integer limit, String mode) {
        Pageable nextPage;

        switch (mode) {
            case "popular": nextPage = PageRequest.of(offset, limit, Sort.by(Sort.Direction.DESC, "postComments"));
                break;
            case "best": nextPage = PageRequest.of(offset, limit, Sort.by(Sort.Direction.DESC, "postVotes"));
                break;
            case "early": nextPage = PageRequest.of(offset, limit, Sort.by(Sort.Direction.ASC, "time"));
                break;
            default: nextPage = PageRequest.of(offset, limit, Sort.by(Sort.Direction.DESC, "time"));
        }

        PostResponse postResponse = new PostResponse(postRepository.getPostByIsActiveEqualsAndModerationStatusEquals(1, ModerationStatus.ACCEPTED, nextPage).getContent());

        List<Post> posts = postResponse.getPosts();

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


            System.out.println(post.getTime().toEpochSecond(ZoneOffset.UTC) + " " + post.getTime());

        }

        return new PostResponse(postRepository.getPostByIsActiveEqualsAndModerationStatusEquals(1, ModerationStatus.ACCEPTED, nextPage).getContent()) ;
    }

    public TagResponse getTags(String query) {

        double allActivePostCount = postRepository.getAllByTimeBefore(LocalDateTime.now()).size();
        List<Tag> allTags = tagRepository.findAll();
        TagResponse tagResponse;
        Tag mostPopularTag;
        double k;

        allTags.sort((a, b) -> b.getPosts().size() - a.getPosts().size());
        mostPopularTag = allTags.get(0);
        k = mostPopularTag.getPosts().size() / allActivePostCount / 1;

        if (query == null) {
            tagResponse = new TagResponse(allTags);

            for (Tag tag : tagResponse.getTags()) {
                tag.setWeight(k * tag.getPosts().size() / allActivePostCount);
            }

        } else {
            tagResponse = new TagResponse(tagRepository.getAllByNameStartingWith(query));

            tagResponse.getTags().sort((a, b) -> b.getPosts().size() - a.getPosts().size());

            for (Tag tag : tagResponse.getTags()) {
                tag.setWeight(k * tag.getPosts().size() / allActivePostCount);
            }
        }

        return tagResponse;
    }
}
