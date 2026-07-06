package com.taskflow_api.task;

import com.taskflow_api.project.Project;
import com.taskflow_api.project.ProjectRepository;
import com.taskflow_api.shared.exception.ResourceNotFoundException;
import com.taskflow_api.task.dto.CreateTaskRequest;
import com.taskflow_api.task.dto.TaskResponse;
import com.taskflow_api.task.dto.UpdateTaskRequest;
import com.taskflow_api.user.User;
import com.taskflow_api.workspace.WorkspaceMember;
import com.taskflow_api.workspace.WorkspaceMemberRepository;
import com.taskflow_api.workspace.WorkspaceRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock private TaskRepository taskRepository;
    @Mock private ProjectRepository projectRepository;
    @Mock private WorkspaceMemberRepository workspaceMemberRepository;
    @Mock private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private TaskService taskService;

    private UUID projectId;
    private UUID workspaceId;
    private UUID taskId;
    private User currentUser;
    private Project project;

    @BeforeEach
    void setUp() {
        projectId = UUID.randomUUID();
        workspaceId = UUID.randomUUID();
        taskId = UUID.randomUUID();
        currentUser = new User();

        project = Project.create("Test Project", "desc", workspaceId, UUID.randomUUID());
    }

    private CreateTaskRequest buildCreateRequest(String name) {
        CreateTaskRequest req = new CreateTaskRequest();
        ReflectionTestUtils.setField(req, "name", name);
        ReflectionTestUtils.setField(req, "description", "desc");
        ReflectionTestUtils.setField(req, "priority", TaskPriority.MEDIUM);
        return req;
    }

    // ── create ────────────────────────────────────────────────────────────────

    @Test
    void create_whenProjectNotFound_shouldThrowResourceNotFoundException() {
        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class,
                () -> taskService.create(projectId, buildCreateRequest("Task"), currentUser));
    }

    @Test
    void create_whenUserNotWorkspaceMember_shouldThrowResourceNotFoundException() {
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(workspaceMemberRepository.existsByWorkspaceIdAndUserId(any(), any())).thenReturn(false);
        assertThrows(ResourceNotFoundException.class,
                () -> taskService.create(projectId, buildCreateRequest("Task"), currentUser));
    }

    @Test
    void create_whenValid_shouldSaveTaskAndPublishEvent() {
        Task saved = Task.create("My Task", "desc", projectId, null, UUID.randomUUID());

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(workspaceMemberRepository.existsByWorkspaceIdAndUserId(any(), any())).thenReturn(true);
        when(taskRepository.save(any())).thenReturn(saved);

        TaskResponse response = taskService.create(projectId, buildCreateRequest("My Task"), currentUser);

        assertNotNull(response);
        assertEquals("My Task", response.getName());
        verify(taskRepository, times(1)).save(any(Task.class));
        verify(eventPublisher, times(1)).publishEvent(any());
    }

    // ── fetchAllTasks ─────────────────────────────────────────────────────────

    @Test
    void fetchAllTasks_whenProjectNotFound_shouldThrowResourceNotFoundException() {
        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class,
                () -> taskService.fetchAllTasks(projectId, currentUser, PageRequest.of(0, 10), null, null, null));
    }

    @Test
    void fetchAllTasks_whenUserNotMember_shouldThrowResourceNotFoundException() {
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(workspaceMemberRepository.existsByWorkspaceIdAndUserId(any(), any())).thenReturn(false);
        assertThrows(ResourceNotFoundException.class,
                () -> taskService.fetchAllTasks(projectId, currentUser, PageRequest.of(0, 10), null, null, null));
    }

    @Test
    @SuppressWarnings("unchecked")
    void fetchAllTasks_whenValid_shouldReturnPage() {
        Task task = Task.create("Task 1", "desc", projectId, null, UUID.randomUUID());
        Page<Task> page = new PageImpl<>(List.of(task));

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(workspaceMemberRepository.existsByWorkspaceIdAndUserId(any(), any())).thenReturn(true);
        when(taskRepository.findAll(any(Specification.class), any(PageRequest.class))).thenReturn(page);

        Page<TaskResponse> result = taskService.fetchAllTasks(projectId, currentUser, PageRequest.of(0, 10), null, null, null);

        assertEquals(1, result.getTotalElements());
    }

    // ── fetchTaskById ─────────────────────────────────────────────────────────

    @Test
    void fetchTaskById_whenProjectNotFound_shouldThrowResourceNotFoundException() {
        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class,
                () -> taskService.fetchTaskById(projectId, taskId, currentUser));
    }

    @Test
    void fetchTaskById_whenUserNotMember_shouldThrowResourceNotFoundException() {
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(workspaceMemberRepository.existsByWorkspaceIdAndUserId(any(), any())).thenReturn(false);
        assertThrows(ResourceNotFoundException.class,
                () -> taskService.fetchTaskById(projectId, taskId, currentUser));
    }

    @Test
    void fetchTaskById_whenTaskNotFound_shouldThrowResourceNotFoundException() {
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(workspaceMemberRepository.existsByWorkspaceIdAndUserId(any(), any())).thenReturn(true);
        when(taskRepository.findByProjectIdAndId(projectId, taskId)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class,
                () -> taskService.fetchTaskById(projectId, taskId, currentUser));
    }

    @Test
    void fetchTaskById_whenValid_shouldReturnTaskResponse() {
        Task task = Task.create("My Task", "desc", projectId, null, UUID.randomUUID());

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(workspaceMemberRepository.existsByWorkspaceIdAndUserId(any(), any())).thenReturn(true);
        when(taskRepository.findByProjectIdAndId(projectId, taskId)).thenReturn(Optional.of(task));

        TaskResponse response = taskService.fetchTaskById(projectId, taskId, currentUser);

        assertNotNull(response);
        assertEquals("My Task", response.getName());
    }

    // ── updateTask ────────────────────────────────────────────────────────────

    @Test
    void updateTask_whenTaskNotFound_shouldThrowResourceNotFoundException() {
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(workspaceMemberRepository.existsByWorkspaceIdAndUserId(any(), any())).thenReturn(true);
        when(taskRepository.findByProjectIdAndId(projectId, taskId)).thenReturn(Optional.empty());

        UpdateTaskRequest req = new UpdateTaskRequest();
        assertThrows(ResourceNotFoundException.class,
                () -> taskService.updateTask(projectId, taskId, req, currentUser));
    }

    @Test
    void updateTask_whenValid_shouldSaveAndPublishEvent() {
        Task task = Task.create("Old Name", "desc", projectId, null, UUID.randomUUID());
        UpdateTaskRequest req = new UpdateTaskRequest();
        ReflectionTestUtils.setField(req, "name", "New Name");

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(workspaceMemberRepository.existsByWorkspaceIdAndUserId(any(), any())).thenReturn(true);
        when(taskRepository.findByProjectIdAndId(projectId, taskId)).thenReturn(Optional.of(task));
        when(taskRepository.save(any())).thenReturn(task);

        TaskResponse response = taskService.updateTask(projectId, taskId, req, currentUser);

        assertNotNull(response);
        verify(taskRepository, times(1)).save(any(Task.class));
        verify(eventPublisher, times(1)).publishEvent(any());
    }

    // ── deleteTask ────────────────────────────────────────────────────────────

    @Test
    void deleteTask_whenProjectNotFound_shouldThrowResourceNotFoundException() {
        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class,
                () -> taskService.deleteTask(projectId, taskId, currentUser));
    }

    @Test
    void deleteTask_whenUserNotMember_shouldThrowResourceNotFoundException() {
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(workspaceMemberRepository.findByWorkspaceIdAndUserId(any(), any())).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class,
                () -> taskService.deleteTask(projectId, taskId, currentUser));
    }

    @Test
    void deleteTask_whenUserIsMemberNotAdmin_shouldThrowResourceNotFoundException() {
        WorkspaceMember member = new WorkspaceMember();
        member.setRole(WorkspaceRole.MEMBER);

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(workspaceMemberRepository.findByWorkspaceIdAndUserId(any(), any())).thenReturn(Optional.of(member));
        assertThrows(ResourceNotFoundException.class,
                () -> taskService.deleteTask(projectId, taskId, currentUser));
    }

    @Test
    void deleteTask_whenOwner_shouldDeleteAndPublishEvent() {
        WorkspaceMember member = new WorkspaceMember();
        member.setRole(WorkspaceRole.OWNER);

        Task task = Task.create("Task", "desc", projectId, null, UUID.randomUUID());

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(workspaceMemberRepository.findByWorkspaceIdAndUserId(any(), any())).thenReturn(Optional.of(member));
        when(taskRepository.findByProjectIdAndId(projectId, taskId)).thenReturn(Optional.of(task));

        taskService.deleteTask(projectId, taskId, currentUser);

        verify(taskRepository, times(1)).deleteById(any());
        verify(eventPublisher, times(1)).publishEvent(any());
    }
}
