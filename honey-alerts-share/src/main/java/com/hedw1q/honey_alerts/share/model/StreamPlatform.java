package com.hedw1q.honey_alerts.share.model;

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;

public enum StreamPlatform {
    TWITCH("Twitch", "https://www.twitch.tv/"),
    GOODGAME("GoodGame", "https://goodgame.ru/channel/"),
    @JsonEnumDefaultValue
    UNKNOWN("Unknown platform", "https://www.youtube.com/watch?v=51IXwqDDLG4");

    final String name;
    final String link;

    StreamPlatform(String name, String link) {
        this.name = name;
        this.link = link;
    }

    public static StreamPlatform parsePlatform(String platform) throws IllegalArgumentException{
        return StreamPlatform.valueOf(platform.toUpperCase());
    }

    public String getName() {
        return name;
    }

    public String getLink() {
        return link;
    }

    @Override
    public String toString() {
        return name;
    }
}
