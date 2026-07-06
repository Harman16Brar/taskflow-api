package com.taskflow_api.workspace;

import com.taskflow_api.shared.response.ApiResponse;
import com.taskflow_api.user.User;
import com.taskflow_api.workspace.dto.CreateWorkspaceRequest;
import com.taskflow_api.workspace.dto.WorkspaceResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/workspaces")
public class WorkspaceController {

    private final WorkspaceService workspaceService;

    public WorkspaceController(WorkspaceService workspaceService) {
        this.workspaceService = workspaceService;
    }

    @GetMapping("/")
    public ResponseEntity<ApiResponse<List<WorkspaceResponse>>> getUserWorkspaces() {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<WorkspaceResponse> workspaceResponses = workspaceService.getUserWorkspaces(currentUser);

        return ResponseEntity.ok(ApiResponse.success(workspaceResponses, "Workspaces fetched successfully"));
    }

    @PostMapping("/")
    public ResponseEntity<ApiResponse<WorkspaceResponse>> createWorkspace(@Valid @RequestBody CreateWorkspaceRequest request) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        WorkspaceResponse workspaceResponse = workspaceService.createWorkspace(request.getName(), currentUser);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(workspaceResponse, "Workspaces created successfully"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<WorkspaceResponse>> getWorkspace(@PathVariable UUID id) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        WorkspaceResponse response = workspaceService.getWorkspace(id, currentUser);
        return ResponseEntity
                .ok(ApiResponse.success(response, "Workspace fetched successfully"));
    }
}
