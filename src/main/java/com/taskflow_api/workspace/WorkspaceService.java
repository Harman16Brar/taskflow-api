package com.taskflow_api.workspace;

import com.taskflow_api.shared.exception.ResourceNotFoundException;
import com.taskflow_api.user.User;
import com.taskflow_api.user.UserRepository;
import com.taskflow_api.workspace.dto.WorkspaceResponse;
import jakarta.transaction.Transactional;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class WorkspaceService {

    private final WorkspaceRepository workspaceRepository;
    private final WorkspaceMemberRepository workspaceMemberRepository;
    private final UserRepository userRepository;

    public WorkspaceService(WorkspaceRepository workspaceRepository,
                            WorkspaceMemberRepository workspaceMemberRepository,
                            UserRepository userRepository) {
        this.workspaceMemberRepository = workspaceMemberRepository;
        this.workspaceRepository = workspaceRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public Workspace createDefaultWorkspace(User user) {
        Workspace workspace = Workspace.create(user.getFirstName() + "'s Workspace", user.getId());
        workspaceRepository.save(workspace);

        WorkspaceMember workspaceMember = WorkspaceMember.create(workspace.getId(), workspace.getOwnerId(), WorkspaceRole.OWNER);
        workspaceMemberRepository.save(workspaceMember);
        return workspace;
    }

    @Transactional
    @CacheEvict(value = "user-workspaces", key = "#user.id")
    public WorkspaceResponse createWorkspace(String name, User user)    {
        Workspace workspace = Workspace.create(name, user.getId());

        Workspace savedWorkspace = workspaceRepository.save(workspace);

        WorkspaceMember workspaceMember = WorkspaceMember.create(workspace.getId(), workspace.getOwnerId(), WorkspaceRole.OWNER);
        workspaceMemberRepository.save(workspaceMember);
        return WorkspaceResponse.builder()
                .workspaceId(savedWorkspace.getId())
                .workspaceName(savedWorkspace.getName())
                .ownerName(user.getFirstName() + " " + user.getLastName())
                .createdAt(savedWorkspace.getCreatedAt())
                .build();
    }

    @Cacheable(value = "user-workspaces", key = "#user.id")
    public List<WorkspaceResponse> getUserWorkspaces(User user) {
        List<Workspace> workspaces = workspaceRepository.findAllByMemberUserId(user.getId());

        return workspaces.stream()
                .map(workspace -> {
                    User owner = userRepository.findById(workspace.getOwnerId())
                            .orElseThrow(() -> new ResourceNotFoundException("User", workspace.getOwnerId().toString()));
                    return WorkspaceResponse.builder()
                            .workspaceId(workspace.getId())
                            .workspaceName(workspace.getName())
                            .ownerName(owner.getFirstName() + " " + owner.getLastName())
                            .createdAt(workspace.getCreatedAt())
                            .build();
                })
                .toList();
    }

    @Cacheable(value = "workspaces", key = "#workspaceId + ':' + #currentUser.id")
    public WorkspaceResponse getWorkspace(UUID workspaceId, User currentUser) {
        Workspace workspace = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new ResourceNotFoundException("Workspace", workspaceId.toString()));

        boolean isMember = workspaceMemberRepository.existsByWorkspaceIdAndUserId(workspaceId, currentUser.getId());
        if (!isMember) {
            throw new ResourceNotFoundException("Workspace", workspaceId.toString());
        }

        User owner = userRepository.findById(workspace.getOwnerId())
                .orElseThrow(() -> new ResourceNotFoundException("User", workspace.getOwnerId().toString()));

        return WorkspaceResponse.builder()
                .workspaceId(workspace.getId())
                .workspaceName(workspace.getName())
                .ownerName(owner.getFirstName() + " " + owner.getLastName())
                .createdAt(workspace.getCreatedAt())
                .build();

    }

}