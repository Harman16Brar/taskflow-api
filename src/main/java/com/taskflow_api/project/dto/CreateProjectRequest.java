package com.taskflow_api.project.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CreateProjectRequest {
    @NotBlank(message = "project name is required")
    private String name;

    private String description;
}
