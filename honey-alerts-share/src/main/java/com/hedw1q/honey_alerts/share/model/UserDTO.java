package com.hedw1q.honey_alerts.share.model;

import jakarta.validation.constraints.NotNull;

public record UserDTO(
        @NotNull(message = "Telegram ID should be not empty")
        Long tgId,
        String tgName) {
}