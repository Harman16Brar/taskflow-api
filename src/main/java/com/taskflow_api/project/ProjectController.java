package com.taskflow_api.project;

import com.taskflow_api.project.dto.CreateProjectRequest;
import com.taskflow_api.project.dto.ProjectResponse;
import com.taskflow_api.project.dto.UpdateProjectRequest;
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
@RequestMapping("/api/v1/workspaces/{workspaceId}/projects")
public class ProjectController {
    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ProjectResponse>> createProject(@Valid @RequestBody CreateProjectRequest request,
                                                                      @PathVariable UUID workspaceId) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        ProjectResponse response = projectService.createProject(workspaceId, request, currentUser);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Project created successfully"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProjectResponse>>> fetchAllprojects(@PathVariable UUID workspaceId) {
        User currentuser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<ProjectResponse> responses = projectService.fetchAllprojects(workspaceId, currentuser);

        return ResponseEntity
                .ok(ApiResponse.success(responses, "Fetched all projects successfully"));
    }

    @GetMapping("/{projectId}")
    public ResponseEntity<ApiResponse<ProjectResponse>> fetchProjectById(@PathVariable UUID workspaceId, @PathVariable UUID projectId) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        ProjectResponse response = projectService.fetchProjectById(workspaceId, projectId, currentUser);

        return ResponseEntity
                .ok(ApiResponse.success(response, "Project fetched successfully"));

    }

    @PutMapping("/{projectId}")
    public ResponseEntity<ApiResponse<ProjectResponse>> updateProject(@PathVariable UUID workspaceId, @PathVariable UUID projectId, @RequestBody UpdateProjectRequest request) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        ProjectResponse response = projectService.updateProject(workspaceId, projectId, currentUser, request);

        return ResponseEntity
                .ok(ApiResponse.success(response, "Project updated successfully"));
    }

    @DeleteMapping("/{projectId}")
    public ResponseEntity<ApiResponse<Void>> deleteProject(@PathVariable UUID workspaceId, @PathVariable UUID projectId) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        projectService.deleteProject(workspaceId, projectId, currentUser);

        return ResponseEntity.ok(ApiResponse.success(null, "Project deleted successfully"));
    }
}
