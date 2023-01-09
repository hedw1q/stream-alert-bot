package com.hedw1q.honey_alerts.streamservice.config;

import jakarta.validation.constraints.NotNull;

public record TwitchAuthData(
        @NotNull
        String oAuthToken,
        @NotNull
        String clientId,
        @NotNull
        String clientSecret) { }
