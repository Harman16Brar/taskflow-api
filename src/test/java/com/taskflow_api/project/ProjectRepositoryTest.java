package com.taskflow_api.project;

import com.taskflow_api.shared.BaseRepositoryTest;
import com.taskflow_api.user.User;
import com.taskflow_api.user.UserRepository;
import com.taskflow_api.workspace.Workspace;
import com.taskflow_api.workspace.WorkspaceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ProjectRepositoryTest extends BaseRepositoryTest {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private WorkspaceRepository workspaceRepository;

    @Autowired
    private UserRepository userRepository;

    private UUID workspaceId;
    private UUID userId;

    @BeforeEach
    void setUp() {
        projectRepository.deleteAll();
        workspaceRepository.deleteAll();
        userRepository.deleteAll();

        User user = new User();
        user.setEmail("test@test.com");
        user.setPassword("password");
        user.setFirstName("Test");
        user.setLastName("User");

        User savedUser = userRepository.save(user);
        Workspace workspace = Workspace.create("Test Workspace", savedUser.getId());
        Workspace savedWorkspace = workspaceRepository.save(workspace);
        workspaceId = savedWorkspace.getId();
        userId = savedUser.getId();
    }

    @Test
    void findAllByWorkspaceId_shouldReturnAllProjects() {
        // Arrange
        Project p1 = Project.create("Project 1", "desc", workspaceId, userId);
        Project p2 = Project.create("Project 2", "desc", workspaceId, userId);
        projectRepository.save(p1);
        projectRepository.save(p2);

        // Act
        List<Project> projects = projectRepository.findAllByWorkspaceId(workspaceId);

        // Assert
        assertEquals(2, projects.size());
    }

    @Test
    void findAllByWorkspaceId_shouldNotReturnSoftDeletedProjects() {
        // Arrange
        Project p1 = Project.create("Project 1", "desc", workspaceId, userId);
        Project p2 = Project.create("Project 2", "desc", workspaceId, userId);
        p2.setDeleted(true); // soft deleted
        projectRepository.save(p1);
        projectRepository.save(p2);

        // Act
        List<Project> projects = projectRepository.findAllByWorkspaceId(workspaceId);

        // Assert
        assertEquals(1, projects.size());
        assertEquals("Project 1", projects.get(0).getName());
    }

    @Test
    void findByWorkspaceIdAndId_shouldReturnProject() {
        // Arrange
        Project p1 = Project.create("Project 1", "desc", workspaceId, userId);
        Project saved = projectRepository.save(p1);

        // Act
        Optional<Project> result = projectRepository.findByWorkspaceIdAndId(workspaceId, saved.getId());

        // Assert
        assertTrue(result.isPresent());
        assertEquals("Project 1", result.get().getName());
    }

    @Test
    void findByWorkspaceIdAndId_shouldReturnEmpty_whenProjectNotFound() {
        // Act
        Optional<Project> result = projectRepository.findByWorkspaceIdAndId(workspaceId, UUID.randomUUID());

        // Assert
        assertTrue(result.isEmpty());
    }
}