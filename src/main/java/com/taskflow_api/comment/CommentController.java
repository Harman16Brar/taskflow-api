package com.taskflow_api.comment;

import com.taskflow_api.comment.dto.CommentResponse;
import com.taskflow_api.comment.dto.CreateCommentRequest;
import com.taskflow_api.shared.response.ApiResponse;
import com.taskflow_api.user.User;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/tasks/{taskId}/comments")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CommentResponse>> addComment(
            @PathVariable UUID taskId,
            @Valid @RequestBody CreateCommentRequest request) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        CommentResponse response = commentService.addComment(taskId, request, currentUser);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Comment added successfully"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CommentResponse>>> getComments(
            @PathVariable UUID taskId) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<CommentResponse> responses = commentService.getComments(taskId, currentUser);
        return ResponseEntity.ok(ApiResponse.success(responses, "Comments fetched successfully"));
    }
}