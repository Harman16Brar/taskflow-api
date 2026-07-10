package com.taskflow_api.task;

import com.taskflow_api.project.Project;
import com.taskflow_api.project.ProjectRepository;
import com.taskflow_api.shared.BaseRepositoryTest;
import com.taskflow_api.user.User;
import com.taskflow_api.user.UserRepository;
import com.taskflow_api.workspace.Workspace;
import com.taskflow_api.workspace.WorkspaceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class TaskRepositoryTest extends BaseRepositoryTest {

    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private WorkspaceRepository workspaceRepository;
    @Autowired
    private UserRepository userRepository;

    private UUID projectId;
    private UUID userId;

    @BeforeEach
    void setUp() {
        taskRepository.deleteAllHard();
        projectRepository.deleteAll();
        workspaceRepository.deleteAll();
        userRepository.deleteAll();

        User user = new User();
        user.setEmail("tasktest@test.com");
        user.setPassword("password");
        user.setFirstName("Task");
        user.setLastName("Tester");
        user = userRepository.save(user);
        userId = user.getId();

        Workspace workspace = Workspace.create("Test Workspace", userId);
        workspace = workspaceRepository.save(workspace);

        Project project = Project.create("Test Project", "desc", workspace.getId(), userId);
        project = projectRepository.save(project);
        projectId = project.getId();
    }

    // ── findAllByProjectId (paginated) ────────────────────────────────────────

    @Test
    void findAllByProjectId_shouldReturnPagedResults() {
        taskRepository.save(Task.create("Task 1", "desc", projectId, null, userId, TaskPriority.HIGH));
        taskRepository.save(Task.create("Task 2", "desc", projectId, null, userId, TaskPriority.HIGH));
        taskRepository.save(Task.create("Task 3", "desc", projectId, null, userId, TaskPriority.HIGH));

        Page<Task> page = taskRepository.findAllByProjectId(projectId, PageRequest.of(0, 2));

        assertEquals(2, page.getContent().size());
        assertEquals(3, page.getTotalElements());
        assertEquals(2, page.getTotalPages());
    }

    @Test
    void findAllByProjectId_shouldReturnEmptyPage_whenNoTasks() {
        Page<Task> page = taskRepository.findAllByProjectId(projectId, PageRequest.of(0, 10));
        assertTrue(page.isEmpty());
    }

    // ── findByProjectIdAndId ──────────────────────────────────────────────────

    @Test
    void findByProjectIdAndId_shouldReturnTask_whenFound() {
        Task saved = taskRepository.save(Task.create("My Task", "desc", projectId, null, userId, TaskPriority.HIGH));
        Optional<Task> result = taskRepository.findByProjectIdAndId(projectId, saved.getId());
        assertTrue(result.isPresent());
        assertEquals("My Task", result.get().getName());
    }

    @Test
    void findByProjectIdAndId_shouldReturnEmpty_whenNotFound() {
        Optional<Task> result = taskRepository.findByProjectIdAndId(projectId, UUID.randomUUID());
        assertTrue(result.isEmpty());
    }

    @Test
    void findByProjectIdAndId_shouldReturnEmpty_whenProjectIdMismatch() {
        Task saved = taskRepository.save(Task.create("My Task", "desc", projectId, null, userId, TaskPriority.HIGH));
        Optional<Task> result = taskRepository.findByProjectIdAndId(UUID.randomUUID(), saved.getId());
        assertTrue(result.isEmpty());
    }

    // ── @SQLRestriction — soft delete filtering ───────────────────────────────

    @Test
    void findAll_shouldExcludeSoftDeletedTasks() {
        Task active = taskRepository.save(Task.create("Active", "desc", projectId, null, userId, TaskPriority.HIGH));
        Task deleted = taskRepository.save(Task.create("Deleted", "desc", projectId, null, userId, TaskPriority.HIGH));
        deleted.setDeleted(true);
        taskRepository.save(deleted);

        Page<Task> page = taskRepository.findAllByProjectId(projectId, PageRequest.of(0, 10));

        assertEquals(1, page.getTotalElements());
        assertEquals("Active", page.getContent().get(0).getName());
    }

    // ── Specifications ────────────────────────────────────────────────────────

    @Test
    void specification_hasStatus_shouldFilterByStatus() {
        taskRepository.save(Task.create("Todo Task", "desc", projectId, null, userId, TaskPriority.HIGH));
        Task inProgress = Task.create("InProgress Task", "desc", projectId, null, userId, TaskPriority.HIGH);
        inProgress.setStatus(TaskStatus.IN_PROGRESS);
        taskRepository.save(inProgress);

        Specification<Task> spec = TaskSpecification.hasProjectId(projectId)
                .and(TaskSpecification.hasStatus(TaskStatus.IN_PROGRESS));

        Page<Task> result = taskRepository.findAll(spec, PageRequest.of(0, 10));

        assertEquals(1, result.getTotalElements());
        assertEquals("InProgress Task", result.getContent().get(0).getName());
    }

    @Test
    void specification_hasPriority_shouldFilterByPriority() {
        Task low = Task.create("Low Task", "desc", projectId, null, userId, TaskPriority.HIGH);
        low.setPriority(TaskPriority.LOW);
        taskRepository.save(low);
        taskRepository.save(Task.create("Medium Task", "desc", projectId, null, userId, TaskPriority.HIGH));

        Specification<Task> spec = TaskSpecification.hasProjectId(projectId)
                .and(TaskSpecification.hasPriority(TaskPriority.LOW));

        Page<Task> result = taskRepository.findAll(spec, PageRequest.of(0, 10));

        assertEquals(1, result.getTotalElements());
        assertEquals("Low Task", result.getContent().get(0).getName());
    }

    @Test
    void specification_hasAssigneeId_shouldFilterByAssignee() {
        // UUID assignee = UUID.randomUUID();
        taskRepository.save(Task.create("Assigned Task", "desc", projectId, userId, userId, TaskPriority.HIGH));
        taskRepository.save(Task.create("Unassigned Task", "desc", projectId, null, userId, TaskPriority.HIGH));

        Specification<Task> spec = TaskSpecification.hasProjectId(projectId)
                .and(TaskSpecification.hasAssigneeId(userId));

        Page<Task> result = taskRepository.findAll(spec, PageRequest.of(0, 10));

        assertEquals(1, result.getTotalElements());
        assertEquals("Assigned Task", result.getContent().get(0).getName());
    }
}
