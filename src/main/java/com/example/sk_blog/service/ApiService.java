package com.example.sk_blog.service;

import com.example.sk_blog.api.response.PostResponse;
import com.example.sk_blog.model.GlobalSetting;
import com.example.sk_blog.api.response.InitResponse;
import com.example.sk_blog.api.response.SettingsResponse;
import com.example.sk_blog.repositories.GlobalSettingsRepository;
import com.example.sk_blog.repositories.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class ApiService {
    private final GlobalSettingsRepository globalSettingsRepository;
    private final PostRepository postRepository;
    private final InitResponse initResponse;

    @Autowired
    public ApiService(GlobalSettingsRepository globalSettingsRepository, PostRepository postRepository, InitResponse initResponse) {
        this.globalSettingsRepository = globalSettingsRepository;
        this.postRepository = postRepository;
        this.initResponse = initResponse;
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

         int size = nextPage.getPageSize();

        System.out.println(size);

        return new PostResponse(postRepository.findAll(nextPage).getContent()) ;
    }
}
