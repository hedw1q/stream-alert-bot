package com.hedw1q.honey_alerts.streamservice.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * @author hedw1q
 */
public enum StreamStatus {
    LIVE,
    @JsonEnumDefaultValue
    OFFLINE
}
