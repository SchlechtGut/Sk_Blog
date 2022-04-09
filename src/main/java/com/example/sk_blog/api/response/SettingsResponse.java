package com.example.sk_blog.api.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class SettingsResponse {

    private final boolean multiuserMode;

    private final boolean postPremoderation;

    private final boolean statisticsIsPublic;

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
