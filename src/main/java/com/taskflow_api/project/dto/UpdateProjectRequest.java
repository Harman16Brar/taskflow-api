package com.taskflow_api.project.dto;

import com.taskflow_api.project.ProjectStatus;
import lombok.Getter;

@Getter
public class UpdateProjectRequest {
    private String name;

    private String description;

    private ProjectStatus status;
}
