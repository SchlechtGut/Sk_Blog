package com.example.sk_blog.service;

import com.example.sk_blog.api.response.*;
import com.example.sk_blog.model.*;
import com.example.sk_blog.repositories.CaptchaCodeRepository;
import com.example.sk_blog.repositories.GlobalSettingsRepository;
import com.example.sk_blog.repositories.PostRepository;
import com.example.sk_blog.repositories.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ApiGeneralService {
    private final GlobalSettingsRepository globalSettingsRepository;
    private final PostRepository postRepository;
    private final InitResponse initResponse;
    private final TagRepository tagRepository;

    @Autowired
    public ApiGeneralService(GlobalSettingsRepository globalSettingsRepository, PostRepository postRepository, InitResponse initResponse, TagRepository tagRepository, CaptchaCodeRepository captchaCodeRepository) {
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

    public TagResponse getTags(String query) {

        double allActivePostCount = postRepository.findAllByTimeBefore(LocalDateTime.now()).size();
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

    public CalendarResponse getCalendarResponse(Integer year) {
        if (year == null) {
            year = LocalDate.now().getYear();
        }

        List<Integer> years = postRepository.findYears();

        List<String> dateAndCount = postRepository.getMapOfDistinctPostDates(year);

        Map<LocalDate, Integer> postsPerDate = new LinkedHashMap<>();

        for (String x : dateAndCount) {
            String[] array = x.split(",");
            LocalDate date = LocalDate.parse(array[0]);
            Integer count = Integer.parseInt(array[1]);

            postsPerDate.put(date, count);
        }

        postsPerDate = postsPerDate.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue, LinkedHashMap::new));

        return new CalendarResponse(years, postsPerDate);
    }

    /////////////////////////private///////////////////////////////////////////////////

}
