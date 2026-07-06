package com.taskflow_api.task.dto;

import com.taskflow_api.task.TaskPriority;
import com.taskflow_api.task.TaskStatus;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class UpdateTaskRequest {
    private String name;
    private String description;
    private TaskStatus status;
    private TaskPriority priority;
    private UUID assigneeId;
    private LocalDateTime dueDate;
}