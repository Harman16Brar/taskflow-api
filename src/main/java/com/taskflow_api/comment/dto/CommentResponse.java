package com.taskflow_api.comment.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class CommentResponse {

    private final UUID id;
    private final UUID taskId;
    private final UUID authorId;
    private final String content;
    private final LocalDateTime createdAt;
}
