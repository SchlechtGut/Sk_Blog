package com.example.sk_blog.api.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
public class CalendarResponse {

    private List<Integer> years;
    private Map<LocalDate, Integer> posts;
}
