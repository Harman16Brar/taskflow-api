package com.taskflow_api.activity;

import com.taskflow_api.project.Project;
import com.taskflow_api.project.ProjectRepository;
import com.taskflow_api.shared.BaseRepositoryTest;
import com.taskflow_api.task.Task;
import com.taskflow_api.task.TaskPriority;
import com.taskflow_api.task.TaskRepository;
import com.taskflow_api.user.User;
import com.taskflow_api.user.UserRepository;
import com.taskflow_api.workspace.Workspace;
import com.taskflow_api.workspace.WorkspaceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ActivityLogRepositoryTest extends BaseRepositoryTest {

    @Autowired
    private ActivityLogRepository activityLogRepository;
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private WorkspaceRepository workspaceRepository;
    @Autowired
    private UserRepository userRepository;

    private UUID taskId;
    private UUID userId;

    @BeforeEach
    void setUp() {
        activityLogRepository.deleteAll();
        taskRepository.deleteAll();
        projectRepository.deleteAll();
        workspaceRepository.deleteAll();
        userRepository.deleteAll();

        User user = new User();
        user.setEmail("activitytest@test.com");
        user.setPassword("password");
        user.setFirstName("Activity");
        user.setLastName("Tester");
        user = userRepository.save(user);
        userId = user.getId();

        Workspace workspace = Workspace.create("Workspace", userId);
        workspace = workspaceRepository.save(workspace);

        Project project = Project.create("Project", "desc", workspace.getId(), userId);
        project = projectRepository.save(project);

        Task task = Task.create("Task", "desc", project.getId(), null, userId, TaskPriority.HIGH);
        task = taskRepository.save(task);
        taskId = task.getId();
    }

    private ActivityLog buildLog(String action, String detail) {
        ActivityLog log = new ActivityLog();
        log.setTaskId(taskId);
        log.setUserId(userId);
        log.setAction(action);
        log.setDetail(detail);
        return log;
    }

    @Test
    void findAllByTaskIdOrderByCreatedAtDesc_shouldReturnLogsForTask() {
        activityLogRepository.save(buildLog("TASK_CREATED", "Task created"));
        activityLogRepository.save(buildLog("TASK_UPDATED", "Status updated"));

        List<ActivityLog> logs = activityLogRepository.findAllByTaskIdOrderByCreatedAtDesc(taskId);

        assertEquals(2, logs.size());
    }

    @Test
    void findAllByTaskIdOrderByCreatedAtDesc_shouldReturnEmpty_whenNoLogs() {
        List<ActivityLog> logs = activityLogRepository.findAllByTaskIdOrderByCreatedAtDesc(taskId);
        assertTrue(logs.isEmpty());
    }

    @Test
    void findAllByTaskIdOrderByCreatedAtDesc_shouldNotReturnLogsForDifferentTask() {
        activityLogRepository.save(buildLog("TASK_CREATED", "Task created"));

        List<ActivityLog> logs = activityLogRepository.findAllByTaskIdOrderByCreatedAtDesc(UUID.randomUUID());

        assertTrue(logs.isEmpty());
    }

    @Test
    void findAllByTaskIdOrderByCreatedAtDesc_shouldReturnAllFieldsCorrectly() {
        activityLogRepository.save(buildLog("TASK_CREATED", "Task created"));

        List<ActivityLog> logs = activityLogRepository.findAllByTaskIdOrderByCreatedAtDesc(taskId);

        assertEquals(1, logs.size());
        ActivityLog log = logs.get(0);
        assertEquals(taskId, log.getTaskId());
        assertEquals(userId, log.getUserId());
        assertEquals("TASK_CREATED", log.getAction());
        assertEquals("Task created", log.getDetail());
        assertNotNull(log.getCreatedAt());
    }
}
