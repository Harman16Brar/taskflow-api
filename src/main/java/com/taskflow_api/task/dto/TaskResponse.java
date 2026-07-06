package com.taskflow_api.task.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class TaskResponse {
    private final UUID id;
    private final String name;
    private final String description;
    private final UUID projectId;
    private final UUID assigneeId;
    private final UUID createdBy;
    private final String status;
    private final String priority;
    private final LocalDateTime dueDate;
    private final LocalDateTime createdAt;
}
