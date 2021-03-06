package com.example.sk_blog.service;

import com.example.sk_blog.api.request.ProfileRequest;
import com.example.sk_blog.api.response.*;
import com.example.sk_blog.model.GlobalSetting;
import com.example.sk_blog.model.Post;
import com.example.sk_blog.model.Tag;
import com.example.sk_blog.model.User;
import com.example.sk_blog.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ApiGeneralService {
    private final GlobalSettingsRepository globalSettingsRepository;
    private final PostRepository postRepository;
    private final InitResponse initResponse;
    private final TagRepository tagRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ResourceStorage resourceStorage;

    @Autowired
    public ApiGeneralService(GlobalSettingsRepository globalSettingsRepository, PostRepository postRepository, InitResponse initResponse, TagRepository tagRepository, CaptchaCodeRepository captchaCodeRepository, PostCommentRepository postCommentRepository, UserRepository userRepository, UserRepository userRepository1, PasswordEncoder passwordEncoder, ResourceStorage resourceStorage) {
        this.globalSettingsRepository = globalSettingsRepository;
        this.postRepository = postRepository;
        this.initResponse = initResponse;
        this.tagRepository = tagRepository;
        this.userRepository = userRepository1;
        this.passwordEncoder = passwordEncoder;
        this.resourceStorage = resourceStorage;
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

        if (allTags.isEmpty()) {
            tagResponse = new TagResponse(allTags);
            return tagResponse;
        }

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

    public StatisticsResponse getUserStatistics() {
        User user = getCurrentUser();
        return getStatistics(user.getPosts());
    }

    public ResponseEntity<?> getGeneralStatistics() {
        if (!getGlobalSettings().isStatisticsIsPublic() && !isModerator()) {
            return  ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
//            return  ResponseEntity.status(HttpStatus.FOUND).location(URI.create("/")).build(); ?????? ????????????????
        }

        List<Post> posts = postRepository.findAll();
        StatisticsResponse statisticsResponse = getStatistics(posts);

        return ResponseEntity.ok(statisticsResponse);
    }

    public TrueOrErrorsResponse editProfileWithoutPhoto(ProfileRequest profileRequest, BindingResult bindingResult) {
        Map<String, String> errors = new LinkedHashMap<>();

        if (bindingResult.hasErrors()) {
            for (ObjectError error : bindingResult.getAllErrors()) {
                String fieldName = ((FieldError) error).getField();
                String errorMessage = error.getDefaultMessage();
                errors.put(fieldName, errorMessage);
            }
        }

        if (!errors.isEmpty()) {
            return new TrueOrErrorsResponse(errors);
        }

        User user = getCurrentUser();

        if (profileRequest.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(profileRequest.getPassword()));
        }

        if (profileRequest.getRemovePhoto().equals(1)) {
            user.setPhoto(null);
        }

        userRepository.save(user);

        return new TrueOrErrorsResponse(true);
    }
    public TrueOrErrorsResponse editProfileWithPhoto(String name, String email, String password, String imagePath) {
        User user = getCurrentUser();

        user.setName(name);
        user.setEmail(email);
        user.setPhoto(imagePath);
        if(password != null) {
            user.setPassword(passwordEncoder.encode(password));
        }

        userRepository.save(user);
        return new TrueOrErrorsResponse(true);
    }

    public TrueOrErrorsResponse checkCredentials(String name, String email, String password) {
        Map<String, String> errors = new LinkedHashMap<>();
        if (name.length() < 2 ) {
            errors.put("name", "?????? ?????????? 2 ????????????????");
        }

        if (!email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            errors.put("email", "???????????????? ???????????? email");
        }

        if (password != null && password.length() < 6) {
            errors.put("password", "???????????? ?????????? 6 ????????????????");
        }

        return new TrueOrErrorsResponse(errors);
    }

    /////////////////////////private///////////////////////////////////////////////////

    private User getCurrentUser() {
        org.springframework.security.core.userdetails.User securityUser =
                (org.springframework.security.core.userdetails.User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userRepository.findByEmail(securityUser.getUsername()).get();
    }

    private boolean isModerator() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();


        if (authentication instanceof UsernamePasswordAuthenticationToken) {
            return getCurrentUser().isModerator();
        } else return false;
    }

    private StatisticsResponse getStatistics(Collection<Post> posts) {
        StatisticsResponse statisticsResponse = new StatisticsResponse();
        statisticsResponse.setPostsCount(posts.size());
        statisticsResponse.setLikesCount(posts.stream().mapToLong(Post::getLikesCount).sum());
        statisticsResponse.setDislikesCount(posts.stream().mapToLong(Post::getDislikesCount).sum());
        statisticsResponse.setViewsCount(posts.stream().mapToLong(Post::getViewCount).sum());
        statisticsResponse.setFirstPublication(posts.stream().mapToLong(x -> x.getTime().atZone(ZoneId.of("UTC")).toInstant().toEpochMilli()).min().getAsLong());

        return statisticsResponse;
    }

    public void changeGlobalSettings(SettingsResponse settings) {
        GlobalSetting multiuserMode = globalSettingsRepository.findByCode("MULTIUSER_MODE");
        multiuserMode.setValue(settings.isMultiuserMode() ? "YES" : "NO");

        GlobalSetting postPremoderation = globalSettingsRepository.findByCode("POST_PREMODERATION");
        postPremoderation.setValue(settings.isPostPremoderation() ? "YES" : "NO");

        GlobalSetting statisticsIsPublic = globalSettingsRepository.findByCode("STATISTICS_IS_PUBLIC");
        statisticsIsPublic.setValue(settings.isStatisticsIsPublic() ? "YES" : "NO");

        globalSettingsRepository.saveAll(List.of(multiuserMode, postPremoderation, statisticsIsPublic));
    }


}
