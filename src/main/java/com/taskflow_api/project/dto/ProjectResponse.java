package com.taskflow_api.project.dto;

import com.taskflow_api.project.Project;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@Jacksonized
public class ProjectResponse implements Serializable {
    private final UUID id;
    private final String name;
    private final String description;
    private final String status;
    private final UUID workspaceId;
    private final UUID createdBy;
    private final LocalDateTime createdAt;
}
