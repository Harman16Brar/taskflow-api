package com.taskflow_api.comment;

import com.taskflow_api.project.Project;
import com.taskflow_api.project.ProjectRepository;
import com.taskflow_api.shared.BaseRepositoryTest;
import com.taskflow_api.task.Task;
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

class CommentRepositoryTest extends BaseRepositoryTest {

    @Autowired private CommentRepository commentRepository;
    @Autowired private TaskRepository taskRepository;
    @Autowired private ProjectRepository projectRepository;
    @Autowired private WorkspaceRepository workspaceRepository;
    @Autowired private UserRepository userRepository;

    private UUID taskId;
    private UUID userId;

    @BeforeEach
    void setUp() {
        commentRepository.deleteAll();
        taskRepository.deleteAll();
        projectRepository.deleteAll();
        workspaceRepository.deleteAll();
        userRepository.deleteAll();

        User user = new User();
        user.setEmail("commenttest@test.com");
        user.setPassword("password");
        user.setFirstName("Comment");
        user.setLastName("Tester");
        user = userRepository.save(user);
        userId = user.getId();

        Workspace workspace = Workspace.create("Workspace", userId);
        workspace = workspaceRepository.save(workspace);

        Project project = Project.create("Project", "desc", workspace.getId(), userId);
        project = projectRepository.save(project);

        Task task = Task.create("Test Task", "desc", project.getId(), null, userId);
        task = taskRepository.save(task);
        taskId = task.getId();
    }

    @Test
    void findAllByTaskId_shouldReturnAllComments() {
        commentRepository.save(Comment.create(taskId, userId, "First comment"));
        commentRepository.save(Comment.create(taskId, userId, "Second comment"));

        List<Comment> result = commentRepository.findAllByTaskId(taskId);

        assertEquals(2, result.size());
    }

    @Test
    void findAllByTaskId_shouldReturnEmptyList_whenNoComments() {
        List<Comment> result = commentRepository.findAllByTaskId(taskId);
        assertTrue(result.isEmpty());
    }

    @Test
    void findAllByTaskId_shouldReturnEmpty_forDifferentTask() {
        commentRepository.save(Comment.create(taskId, userId, "A comment"));

        List<Comment> result = commentRepository.findAllByTaskId(UUID.randomUUID());

        assertTrue(result.isEmpty());
    }

    @Test
    void findAllByTaskId_shouldExcludeSoftDeletedComments() {
        Comment active = commentRepository.save(Comment.create(taskId, userId, "Active"));
        Comment deleted = commentRepository.save(Comment.create(taskId, userId, "Deleted"));
        deleted.setDeleted(true);
        commentRepository.save(deleted);

        List<Comment> result = commentRepository.findAllByTaskId(taskId);

        assertEquals(1, result.size());
        assertEquals("Active", result.get(0).getContent());
    }
}
