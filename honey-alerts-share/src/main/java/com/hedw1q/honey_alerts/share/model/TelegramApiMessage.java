package com.hedw1q.honey_alerts.share.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record TelegramApiMessage(
        @JsonProperty("tgIds")
        @NotEmpty
        List<Long> tgIds,

        @JsonProperty("message")
        @NotBlank
        String message,
        @JsonProperty("attachment")
        String attachmentURL) {
}
