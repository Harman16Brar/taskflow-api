package com.taskflow_api.project;

import com.taskflow_api.project.dto.CreateProjectRequest;
import com.taskflow_api.project.dto.ProjectResponse;
import com.taskflow_api.project.dto.UpdateProjectRequest;
import com.taskflow_api.shared.exception.ResourceNotFoundException;
import com.taskflow_api.shared.exception.UnauthorizedAccessException;
import com.taskflow_api.user.User;
import com.taskflow_api.workspace.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final WorkspaceRepository workspaceRepository;
    private final WorkspaceMemberRepository workspaceMemberRepository;

    public ProjectService(ProjectRepository projectRepository, WorkspaceRepository workspaceRepository, WorkspaceMemberRepository workspaceMemberRepository) {
        this.projectRepository = projectRepository;
        this.workspaceRepository = workspaceRepository;
        this.workspaceMemberRepository = workspaceMemberRepository;
    }

    @CacheEvict(value = "workspace-projects", key = "#workspaceId + ':' + #currentUser.id")
    public ProjectResponse createProject(UUID workspaceId, CreateProjectRequest request, User currentUser) {
        log.info("sdfdffd");
        WorkspaceMember member = workspaceMemberRepository
                .findByWorkspaceIdAndUserId(workspaceId, currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Workspace", workspaceId.toString()));

        if (member.getRole() != WorkspaceRole.OWNER && member.getRole() != WorkspaceRole.ADMIN) {
            throw new UnauthorizedAccessException();
        }

        Project project = Project.create(request.getName(), request.getDescription(), workspaceId, currentUser.getId());
        Project savedProject = projectRepository.save(project);

        return toResponse(savedProject);

    }

    @Cacheable(value = "workspace-projects", key = "#workspaceId + ':' + #currentuser.id")
    public List<ProjectResponse> fetchAllprojects(UUID workspaceId, User currentuser) {
        boolean isMember = workspaceMemberRepository.existsByWorkspaceIdAndUserId(workspaceId, currentuser.getId());

        if (!isMember) {
            throw new ResourceNotFoundException("WorkspaceMember", workspaceId.toString());
        }
        List<Project> projects = projectRepository.findAllByWorkspaceId(workspaceId);

        return projects.stream()
                .map(project -> {
                    return toResponse(project);
                }).toList();
    }

    @Cacheable(value = "projects", key = "#workspaceId + ':' + #projectId")
    public ProjectResponse fetchProjectById(UUID workspaceId, UUID projectId, User currentUser) {
        boolean isMember = workspaceMemberRepository.existsByWorkspaceIdAndUserId(workspaceId, currentUser.getId());

        if (!isMember) {
            throw new UnauthorizedAccessException();
        }

        //find project by projectId within workspace with workspaceId
        Project project = projectRepository.findByWorkspaceIdAndId(workspaceId, projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project", projectId.toString()));


        //project is also present in given workspace and user is also valid
        //return the project

        return toResponse(project);
    }

    @Caching(evict = {
            @CacheEvict(value = "workspace-projects", key = "#workspaceId + ':' + #currentUser.id"),
            @CacheEvict(value = "projects", key = "#workspaceId + ':' + #projectId")
    })
    public ProjectResponse updateProject(UUID workspaceId, UUID projectId, User currentUser, UpdateProjectRequest request) {
        WorkspaceMember member = workspaceMemberRepository.findByWorkspaceIdAndUserId(workspaceId, currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Workspace", workspaceId.toString()));

        //check currentUser role: only ADMIN/OWNER can update
        if (!member.getRole().equals(WorkspaceRole.OWNER) && !member.getRole().equals(WorkspaceRole.ADMIN)) {
            throw new UnauthorizedAccessException();
        }

        Project project = projectRepository.findByWorkspaceIdAndId(workspaceId, projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project", projectId.toString()));

        //update date
        if (request.getName() != null) project.setName(request.getName());
        if (request.getDescription() != null) project.setDescription(request.getDescription());
        if (request.getStatus() != null) project.setStatus(request.getStatus());

        Project savedProject = projectRepository.save(project);
        return toResponse(savedProject);

    }

    @Caching(evict = {
            @CacheEvict(value = "workspace-projects", key = "#workspaceId + ':' + #currentUser.id"),
            @CacheEvict(value = "projects", key = "#workspaceId + ':' + #projectId")
    })
    public void deleteProject(UUID workspaceId, UUID projectId, User currentUser) {
        WorkspaceMember member = workspaceMemberRepository.findByWorkspaceIdAndUserId(workspaceId, currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Workspace", workspaceId.toString()));

        if (!member.getRole().equals(WorkspaceRole.OWNER)) {
            throw new UnauthorizedAccessException();
        }

        Project project = projectRepository.findByWorkspaceIdAndId(workspaceId, projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project", projectId.toString()));

        project.setDeleted(true);
        project.setDeletedAt(LocalDateTime.now());

        projectRepository.save(project);
    }

    // private helper — only used inside this class
    private ProjectResponse toResponse(Project project) {
        return ProjectResponse.builder()
                .id(project.getId())
                .name(project.getName())
                .description(project.getDescription())
                .workspaceId(project.getWorkspaceId())
                .status(project.getStatus().toString())
                .createdBy(project.getCreatedBy())
                .createdAt(project.getCreatedAt())
                .build();
    }


}
