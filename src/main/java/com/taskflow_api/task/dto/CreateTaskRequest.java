package com.taskflow_api.task.dto;

import com.taskflow_api.task.TaskPriority;
import com.taskflow_api.task.TaskStatus;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class CreateTaskRequest {

    @NotBlank(message = "task name is required")
    private String name;

    private String description;

    private UUID assigneeId;

    private TaskPriority priority = TaskPriority.MEDIUM;

    private LocalDateTime dueDate;


}
