package com.taskflow_api.comment.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class CreateCommentRequest {
    @NotBlank(message = "content is required")
    private String content;
}
