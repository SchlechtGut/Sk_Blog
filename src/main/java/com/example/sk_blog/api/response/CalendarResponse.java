package com.example.sk_blog.api.response;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class CalendarResponse {

    private List<Integer> years;
    private LinkedHashMap<LocalDate, Integer> posts;

    public CalendarResponse(List<Integer> years, LinkedHashMap<LocalDate, Integer> posts) {
        this.years = years;
        this.posts = posts;
    }

    public List<Integer> getYears() {
        return years;
    }

    public void setYears(List<Integer> years) {
        this.years = years;
    }

    public HashMap<LocalDate, Integer> getPosts() {
        return posts;
    }

    public void setPosts(LinkedHashMap<LocalDate, Integer> posts) {
        this.posts = posts;
    }
}
