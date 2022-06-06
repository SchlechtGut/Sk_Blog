package com.example.sk_blog.api.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class SettingsResponse {

    @JsonProperty("MULTIUSER_MODE")
    private final boolean multiuserMode;

    @JsonProperty("POST_PREMODERATION")
    private final boolean postPremoderation;

    @JsonProperty("STATISTICS_IS_PUBLIC")
    private final boolean statisticsIsPublic;
}
