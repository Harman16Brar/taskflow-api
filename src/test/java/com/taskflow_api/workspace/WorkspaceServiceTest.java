package com.taskflow_api.workspace;

import com.taskflow_api.shared.exception.ResourceNotFoundException;
import com.taskflow_api.user.User;
import com.taskflow_api.user.UserRepository;
import com.taskflow_api.workspace.dto.WorkspaceResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WorkspaceServiceTest {

    @Mock
    private WorkspaceRepository workspaceRepository;

    @Mock
    private WorkspaceMemberRepository workspaceMemberRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private WorkspaceService workspaceService;

    @Test
    void createWorkspace_whenCalled_shouldReturnWorkspaceResponse() {
        // Arrange
        User currentUser = new User();

        Workspace savedWorkspace = new Workspace();

        when(workspaceRepository.save(any())).thenReturn(savedWorkspace);
        when(workspaceMemberRepository.save(any())).thenReturn(null);

        // Act
        WorkspaceResponse response = workspaceService.createWorkspace("Test Workspace", currentUser);

        // Assert
        assertNotNull(response);
        verify(workspaceRepository, times(1)).save(any(Workspace.class));
        verify(workspaceMemberRepository, times(1)).save(any(WorkspaceMember.class));
    }

    @Test
    void getUserWorkspaces_whenCalled_shouldReturnWorkspaceList() {
        // Arrange
        User currentUser = new User();
        Workspace workspace = new Workspace();
        User owner = new User();

        when(workspaceRepository.findAllByMemberUserId(any()))
                .thenReturn(List.of(workspace));

        when(userRepository.findById(any()))
                .thenReturn(Optional.of(owner));

        // Act
        List<WorkspaceResponse> response = workspaceService.getUserWorkspaces(currentUser);

        // Assert
        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Test
    void getWorkspace_whenUserIsNotMember_shouldThrowResourceNotFoundException() {
        // Arrange
        UUID workspaceId = UUID.randomUUID();
        User currentUser = new User();

        when(workspaceRepository.findById(any()))
                .thenReturn(Optional.of(new Workspace()));

        when(workspaceMemberRepository.existsByWorkspaceIdAndUserId(any(), any()))
                .thenReturn(false);

        // Act + Assert
        assertThrows(ResourceNotFoundException.class, () ->
                workspaceService.getWorkspace(workspaceId, currentUser)
        );
    }

    @Test
    void getWorkspace_whenUserIsMember_shouldReturnWorkspaceResponse() {
        // Arrange
        UUID workspaceId = UUID.randomUUID();
        User currentUser = new User();
        User owner = new User();
        Workspace workspace = new Workspace();

        when(workspaceRepository.findById(any()))
                .thenReturn(Optional.of(workspace));

        when(workspaceMemberRepository.existsByWorkspaceIdAndUserId(any(), any()))
                .thenReturn(true);

        when(userRepository.findById(any()))
                .thenReturn(Optional.of(owner));

        // Act
        WorkspaceResponse response = workspaceService.getWorkspace(workspaceId, currentUser);

        // Assert
        assertNotNull(response);
    }
}