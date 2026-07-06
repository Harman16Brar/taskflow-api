package com.taskflow_api.activity.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class ActivityLogResponse {
    private final UUID id;
    private final UUID taskId;
    private final UUID userId;
    private final String action;
    private final String detail;
    private final LocalDateTime createdAt;
}
