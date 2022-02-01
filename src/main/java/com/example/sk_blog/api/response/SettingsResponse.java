package com.example.sk_blog.api.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SettingsResponse {

    private final boolean multiuserMode;

    private final boolean postPremoderation;

    private final boolean statisticsIsPublic;

    public SettingsResponse(boolean multiuser_mode, boolean post_premoderation, boolean statistics_is_public) {
        multiuserMode = multiuser_mode;
        postPremoderation = post_premoderation;
        statisticsIsPublic = statistics_is_public;
    }

    @JsonProperty("MULTIUSER_MODE")
    public boolean getMultiuserMode() {
        return multiuserMode;
    }

    @JsonProperty("POST_PREMODERATION")
    public boolean getPostPremoderation() {
        return postPremoderation;
    }

    @JsonProperty("STATISTICS_IS_PUBLIC")
    public boolean getStatisticsIsPublic() {
        return statisticsIsPublic;
    }
}
