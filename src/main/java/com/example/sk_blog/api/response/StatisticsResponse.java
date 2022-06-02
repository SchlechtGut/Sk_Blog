package com.example.sk_blog.api.response;

import lombok.Data;

@Data
public class StatisticsResponse {

    private long postsCount;
    private long likesCount;
    private long dislikesCount;
    private long viewsCount;
    private long firstPublication;
}
