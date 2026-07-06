package com.taskflow_api.workspace.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class CreateWorkspaceRequest {
    @NotBlank(message = "workspace name is required")
    private String name;
}
