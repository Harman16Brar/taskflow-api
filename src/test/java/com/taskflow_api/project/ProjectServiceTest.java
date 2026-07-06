package com.taskflow_api.project;

import com.taskflow_api.project.dto.CreateProjectRequest;
import com.taskflow_api.project.dto.ProjectResponse;
import com.taskflow_api.shared.exception.ResourceNotFoundException;
import com.taskflow_api.shared.exception.UnauthorizedAccessException;
import com.taskflow_api.user.User;
import com.taskflow_api.workspace.WorkspaceMember;
import com.taskflow_api.workspace.WorkspaceMemberRepository;
import com.taskflow_api.workspace.WorkspaceRepository;
import com.taskflow_api.workspace.WorkspaceRole;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
public class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private WorkspaceRepository workspaceRepository;

    @Mock
    private WorkspaceMemberRepository workspaceMemberRepository;

    @InjectMocks
    private ProjectService projectService;


    @Test
    void createProject_whenMemberNotFound_shouldThrowResourceNotFoundException() {
        //Arrange
        UUID workspaceId = UUID.randomUUID();
        UUID projectId = UUID.randomUUID();
        User currentUser = new User();
        CreateProjectRequest request = new CreateProjectRequest("Test Project", "Test Description");

        when(workspaceMemberRepository.findByWorkspaceIdAndUserId(any(), any()))
                .thenReturn(Optional.empty());

        //Act + Assert
        assertThrows(ResourceNotFoundException.class,
                () -> projectService.createProject(workspaceId, request, currentUser));
    }

    @Test
    void createProject_whenUserIsNotOwner_NotAdmin_shouldThrowUnauthorizedAccessException() {
        //Arrange
        UUID workspaceId = UUID.randomUUID();
        UUID projectId = UUID.randomUUID();
        User currentUser = new User();
        CreateProjectRequest request = new CreateProjectRequest("Test Project", "Test Description");

        WorkspaceMember member = new WorkspaceMember();
        member.setRole(WorkspaceRole.MEMBER);

        when(workspaceMemberRepository.findByWorkspaceIdAndUserId(any(), any()))
                .thenReturn(Optional.of(member));

        //Act + Assert
        assertThrows(UnauthorizedAccessException.class,
                () -> projectService.createProject(workspaceId, request, currentUser));
    }

    @Test
    void createProject_whenOwner_shouldReturnProjectResponse() {
        //Arrange
        UUID workspaceId = UUID.randomUUID();
        UUID projectId = UUID.randomUUID();
        User currentUser = new User();
        CreateProjectRequest request = new CreateProjectRequest("Test Project", "Test Description");

        WorkspaceMember member = new WorkspaceMember();
        member.setRole(WorkspaceRole.OWNER);

        Project savedProject = Project.create("Test Project", "Test Description", workspaceId, UUID.randomUUID());


        when(workspaceMemberRepository.findByWorkspaceIdAndUserId(any(), any()))
                .thenReturn(Optional.of(member));

        when(projectRepository.save(any()))
                .thenReturn(savedProject);

        //Act
        ProjectResponse response = projectService.createProject(workspaceId, request, currentUser);

        //Assert
        assertNotNull(response);
        assertEquals("Test Project", response.getName());
        verify(projectRepository, times(1)).save(any(Project.class));
    }

    /// /////////////////////////////////////////////////////////////////
    @Test
    void fetchProjectById_whenUserNotMember_shouldThrowUnauthorizedAccessException() {
        //Arrange
        UUID workspaceId = UUID.randomUUID();
        UUID projectId = UUID.randomUUID();
        User currentUser = new User();

        when(workspaceMemberRepository.existsByWorkspaceIdAndUserId(any(), any()))
                .thenReturn(false);

        assertThrows(UnauthorizedAccessException.class,
                () -> projectService.fetchProjectById(workspaceId, projectId, currentUser));
    }

    @Test
    void fetchProjectById_whenProjectNotFound_shouldThrowResourceNotFoundException() {
        //Arrange
        UUID workspaceId = UUID.randomUUID();
        UUID projectId = UUID.randomUUID();
        User currentUser = new User();

        when(workspaceMemberRepository.existsByWorkspaceIdAndUserId(any(), any()))
                .thenReturn(true);

        when(projectRepository.findByWorkspaceIdAndId(any(), any()))
                .thenReturn(Optional.empty());

        //Act + Assert
        assertThrows(ResourceNotFoundException.class,
                () -> projectService.fetchProjectById(workspaceId, projectId, currentUser));
    }

    @Test
    void fetchProjectById_whenUserIsMember_ProjectFound_shouldFetchProjectById() {
        //Arrange
        UUID workspaceId = UUID.randomUUID();
        UUID projectId = UUID.randomUUID();
        User currentUser = new User();

        Project project = Project.create("Test Project", "desc", workspaceId, UUID.randomUUID());

        when(workspaceMemberRepository.existsByWorkspaceIdAndUserId(any(), any()))
                .thenReturn(true);

        when(projectRepository.findByWorkspaceIdAndId(any(), any()))
                .thenReturn(Optional.of(project));

        //Act
        ProjectResponse response = projectService.fetchProjectById(workspaceId, projectId, currentUser);

        //Assert
        assertEquals("Test Project", response.getName());
    }

    /// /////////////////////////////////////////////////////
    @Test
    void fetchAllprojects_whenUserIsNotMember_shouldThrowResourceNotFoundException() {
        //Arrange
        UUID workspaceId = UUID.randomUUID();
        User currentUser = new User();

        when(workspaceMemberRepository.existsByWorkspaceIdAndUserId(any(), any()))
                .thenReturn(false);

        assertThrows(ResourceNotFoundException.class,
                () -> projectService.fetchAllprojects(workspaceId, currentUser));
    }

    @Test
    void fetchAllprojects_whenUserIsMember_shouldReturnProjectList() {
        //Arrange
        UUID workspaceId = UUID.randomUUID();
        User currentUser = new User();

        Project project1 = new Project();
        Project project2 = new Project();

        when(workspaceMemberRepository.existsByWorkspaceIdAndUserId(any(), any()))
                .thenReturn(true);

        when(projectRepository.findAllByWorkspaceId(any()))
                .thenReturn(List.of(project1, project2));

        //Act
        List<ProjectResponse> responseList = projectService.fetchAllprojects(workspaceId, currentUser);

        //Assert
        assertEquals(2, responseList.size());
    }

    /// ///////////////////////////////////////////////////
    @Test
    void deleteProject_whenMemberNotFound_shouldThrowResourceNotFoundException() {
        // Arrange
        UUID workspaceId = UUID.randomUUID();
        UUID projectId = UUID.randomUUID();
        User currentUser = new User();

        when(workspaceMemberRepository.findByWorkspaceIdAndUserId(any(), any()))
                .thenReturn(Optional.empty());

        //Act + Assert
        assertThrows(ResourceNotFoundException.class,
                () -> projectService.deleteProject(workspaceId, projectId, currentUser));
    }

    @Test
    void deleteProject_whenUserIsNotOwner_shouldThrowUnauthorizedAccessException() {

        //Arrage
        UUID workspaceId = UUID.randomUUID();
        UUID projectId = UUID.randomUUID();
        User currentUser = new User();

        WorkspaceMember member = new WorkspaceMember();
        member.setRole(WorkspaceRole.MEMBER);

        when(workspaceMemberRepository.findByWorkspaceIdAndUserId(any(), any()))
                .thenReturn(Optional.of(member));

        //Act + Assert
        assertThrows(UnauthorizedAccessException.class,
                () -> projectService.deleteProject(workspaceId, projectId, currentUser));
    }

    @Test
    void deleteProject_whenProjectNotFound_shouldThrowResourceNotFoundException() {
        // Arrange
        UUID workspaceId = UUID.randomUUID();
        UUID projectId = UUID.randomUUID();
        User currentUser = new User();

        WorkspaceMember member = new WorkspaceMember();
        member.setRole(WorkspaceRole.OWNER);

        when(workspaceMemberRepository.findByWorkspaceIdAndUserId(any(), any()))
                .thenReturn(Optional.of(member));

        when(projectRepository.findByWorkspaceIdAndId(workspaceId, projectId))
                .thenReturn(Optional.empty());

        //Act + Assert
        assertThrows(ResourceNotFoundException.class,
                () -> projectService.deleteProject(workspaceId, projectId, currentUser));


    }

    @Test
    void deleteProject_whenOwner_shouldSoftDeleteAndNeverCallDeleteById() {

        // Arrange
        UUID workspaceId = UUID.randomUUID();
        UUID projectId = UUID.randomUUID();
        User currentUser = new User();

        WorkspaceMember member = new WorkspaceMember();
        member.setRole(WorkspaceRole.OWNER);

        Project project = new Project();

        when(workspaceMemberRepository.findByWorkspaceIdAndUserId(any(), any()))
                .thenReturn(Optional.of(member));

        when(projectRepository.findByWorkspaceIdAndId(any(), any()))
                .thenReturn(Optional.of(project));

        when(projectRepository.save(any())).thenReturn(project);

        // Act
        projectService.deleteProject(workspaceId, projectId, currentUser);

        // Assert
        verify(projectRepository, times(1)).save(any(Project.class));
        verify(projectRepository, never()).deleteById(any());

        assertTrue(project.getDeleted());
        assertNotNull(project.getDeletedAt());
    }
}
