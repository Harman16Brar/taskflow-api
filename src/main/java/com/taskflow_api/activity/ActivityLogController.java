package com.taskflow_api.activity;

import com.taskflow_api.activity.dto.ActivityLogResponse;
import com.taskflow_api.shared.response.ApiResponse;
import com.taskflow_api.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/tasks/{taskId}/activity")
@RequiredArgsConstructor
public class ActivityLogController {

    private final ActivityLogService activityLogService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ActivityLogResponse>>> getTaskActivity(
            @PathVariable UUID taskId) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(ApiResponse.success(
                activityLogService.getTaskActivity(taskId, currentUser),
                "Activity fetched successfully"));
    }
}