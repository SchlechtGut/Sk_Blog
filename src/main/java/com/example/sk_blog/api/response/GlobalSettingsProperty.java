package com.example.sk_blog.api.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GlobalSettingsProperty {

    private final boolean MULTIUSER_MODE;

    private final boolean POST_PREMODERATION;

    private final boolean STATISTICS_IS_PUBLIC;

    public GlobalSettingsProperty(boolean multiuser_mode, boolean post_premoderation, boolean statistics_is_public) {
        MULTIUSER_MODE = multiuser_mode;
        POST_PREMODERATION = post_premoderation;
        STATISTICS_IS_PUBLIC = statistics_is_public;
    }

    @JsonProperty("MULTIUSER_MODE")
    public boolean getMULTIUSER_MODE() {
        return MULTIUSER_MODE;
    }

    @JsonProperty("POST_PREMODERATION")
    public boolean getPOST_PREMODERATION() {
        return POST_PREMODERATION;
    }

    @JsonProperty("STATISTICS_IS_PUBLIC")
    public boolean getSTATISTICS_IS_PUBLIC() {
        return STATISTICS_IS_PUBLIC;
    }
}
