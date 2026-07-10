package com.taskflow_api.activity;

import com.taskflow_api.activity.dto.ActivityLogResponse;
import com.taskflow_api.project.Project;
import com.taskflow_api.project.ProjectRepository;
import com.taskflow_api.shared.exception.ResourceNotFoundException;
import com.taskflow_api.task.Task;
import com.taskflow_api.task.TaskPriority;
import com.taskflow_api.task.TaskRepository;
import com.taskflow_api.user.User;
import com.taskflow_api.workspace.WorkspaceMemberRepository;
import org.junit.jupiter.api.BeforeEach;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ActivityLogServiceTest {

    @Mock private ActivityLogRepository activityLogRepository;
    @Mock private TaskRepository taskRepository;
    @Mock private ProjectRepository projectRepository;
    @Mock private WorkspaceMemberRepository workspaceMemberRepository;

    @InjectMocks
    private ActivityLogService activityLogService;

    private UUID taskId;
    private UUID projectId;
    private UUID workspaceId;
    private User currentUser;
    private Task task;
    private Project project;

    @BeforeEach
    void setUp() {
        taskId = UUID.randomUUID();
        projectId = UUID.randomUUID();
        workspaceId = UUID.randomUUID();
        currentUser = new User();
        task = Task.create("Task", "desc", projectId, null, UUID.randomUUID(), TaskPriority.HIGH);
        project = Project.create("Project", "desc", workspaceId, UUID.randomUUID());
    }

    @Test
    void getTaskActivity_whenTaskNotFound_shouldThrowResourceNotFoundException() {
        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class,
                () -> activityLogService.getTaskActivity(taskId, currentUser));
    }

    @Test
    void getTaskActivity_whenProjectNotFound_shouldThrowResourceNotFoundException() {
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class,
                () -> activityLogService.getTaskActivity(taskId, currentUser));
    }

    @Test
    void getTaskActivity_whenUserNotMember_shouldThrowResourceNotFoundException() {
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(workspaceMemberRepository.existsByWorkspaceIdAndUserId(any(), any())).thenReturn(false);
        assertThrows(ResourceNotFoundException.class,
                () -> activityLogService.getTaskActivity(taskId, currentUser));
    }

    @Test
    void getTaskActivity_whenValid_shouldReturnLogs() {
        ActivityLog log = new ActivityLog();
        log.setTaskId(taskId);
        log.setUserId(UUID.randomUUID());
        log.setAction("TASK_CREATED");
        log.setDetail("Task created");

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(workspaceMemberRepository.existsByWorkspaceIdAndUserId(any(), any())).thenReturn(true);
        when(activityLogRepository.findAllByTaskIdOrderByCreatedAtDesc(taskId)).thenReturn(List.of(log));

        List<ActivityLogResponse> result = activityLogService.getTaskActivity(taskId, currentUser);

        assertEquals(1, result.size());
        assertEquals("TASK_CREATED", result.get(0).getAction());
        assertEquals("Task created", result.get(0).getDetail());
    }

    @Test
    void getTaskActivity_whenNoLogs_shouldReturnEmptyList() {
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(workspaceMemberRepository.existsByWorkspaceIdAndUserId(any(), any())).thenReturn(true);
        when(activityLogRepository.findAllByTaskIdOrderByCreatedAtDesc(taskId)).thenReturn(List.of());

        List<ActivityLogResponse> result = activityLogService.getTaskActivity(taskId, currentUser);

        assertTrue(result.isEmpty());
    }
}
