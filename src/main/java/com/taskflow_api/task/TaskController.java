package com.taskflow_api.task;

import com.taskflow_api.shared.response.ApiResponse;
import com.taskflow_api.task.dto.CreateTaskRequest;
import com.taskflow_api.task.dto.TaskResponse;
import com.taskflow_api.task.dto.UpdateTaskRequest;
import com.taskflow_api.user.User;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/projects/{projectId}/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<TaskResponse>> createTask(
            @PathVariable UUID projectId,
            @Valid @RequestBody CreateTaskRequest request) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(taskService.create(projectId, request, currentUser), "Task created successfully"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<TaskResponse>>> fetchAllTasks(
            @PathVariable UUID projectId,
            @RequestParam(required = false) TaskStatus status,
            @RequestParam(required = false) TaskPriority priority,
            @RequestParam(required = false) UUID assigneeId,
            @ParameterObject @PageableDefault(size = 10, sort = "createdAt",
                    direction = Sort.Direction.DESC) Pageable pageable) {

        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(ApiResponse.success(
                taskService.fetchAllTasks(projectId, currentUser, pageable, status, priority, assigneeId),
                "Tasks fetched successfully"));
    }

    @GetMapping("/{taskId}")
    public ResponseEntity<ApiResponse<TaskResponse>> fetchTaskById(
            @PathVariable UUID projectId,
            @PathVariable UUID taskId) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(ApiResponse.success(taskService.fetchTaskById(projectId, taskId, currentUser), "Task fetched successfully"));
    }

    @PutMapping("/{taskId}")
    public ResponseEntity<ApiResponse<TaskResponse>> updateTask(
            @PathVariable UUID projectId,
            @PathVariable UUID taskId,
            @RequestBody UpdateTaskRequest request) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(ApiResponse.success(taskService.updateTask(projectId, taskId, request, currentUser), "Task updated successfully"));
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<ApiResponse<Void>> deleteTask(
            @PathVariable UUID projectId,
            @PathVariable UUID taskId) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        taskService.deleteTask(projectId, taskId, currentUser);
        return ResponseEntity.ok(ApiResponse.success(null, "Task deleted successfully"));
    }
}