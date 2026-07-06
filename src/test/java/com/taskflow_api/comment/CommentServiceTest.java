package com.taskflow_api.comment;

import com.taskflow_api.comment.dto.CommentResponse;
import com.taskflow_api.comment.dto.CreateCommentRequest;
import com.taskflow_api.project.Project;
import com.taskflow_api.project.ProjectRepository;
import com.taskflow_api.shared.exception.ResourceNotFoundException;
import com.taskflow_api.task.Task;
import com.taskflow_api.task.TaskRepository;
import com.taskflow_api.user.User;
import com.taskflow_api.workspace.WorkspaceMemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Mock private CommentRepository commentRepository;
    @Mock private TaskRepository taskRepository;
    @Mock private WorkspaceMemberRepository workspaceMemberRepository;
    @Mock private ProjectRepository projectRepository;

    @InjectMocks
    private CommentService commentService;

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

        task = Task.create("Task", "desc", projectId, null, UUID.randomUUID());
        project = Project.create("Project", "desc", workspaceId, UUID.randomUUID());
    }

    private CreateCommentRequest buildCommentRequest(String content) {
        CreateCommentRequest req = new CreateCommentRequest();
        ReflectionTestUtils.setField(req, "content", content);
        return req;
    }

    // ── addComment ────────────────────────────────────────────────────────────

    @Test
    void addComment_whenTaskNotFound_shouldThrowResourceNotFoundException() {
        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class,
                () -> commentService.addComment(taskId, buildCommentRequest("Hello"), currentUser));
    }

    @Test
    void addComment_whenProjectNotFound_shouldThrowResourceNotFoundException() {
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class,
                () -> commentService.addComment(taskId, buildCommentRequest("Hello"), currentUser));
    }

    @Test
    void addComment_whenUserNotWorkspaceMember_shouldThrowResourceNotFoundException() {
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(workspaceMemberRepository.existsByWorkspaceIdAndUserId(any(), any())).thenReturn(false);
        assertThrows(ResourceNotFoundException.class,
                () -> commentService.addComment(taskId, buildCommentRequest("Hello"), currentUser));
    }

    @Test
    void addComment_whenValid_shouldSaveAndReturnCommentResponse() {
        Comment savedComment = Comment.create(taskId, UUID.randomUUID(), "Hello");

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(workspaceMemberRepository.existsByWorkspaceIdAndUserId(any(), any())).thenReturn(true);
        when(commentRepository.save(any())).thenReturn(savedComment);

        CommentResponse response = commentService.addComment(taskId, buildCommentRequest("Hello"), currentUser);

        assertNotNull(response);
        assertEquals("Hello", response.getContent());
        verify(commentRepository, times(1)).save(any(Comment.class));
    }

    // ── getComments ───────────────────────────────────────────────────────────

    @Test
    void getComments_whenTaskNotFound_shouldThrowResourceNotFoundException() {
        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class,
                () -> commentService.getComments(taskId, currentUser));
    }

    @Test
    void getComments_whenUserNotMember_shouldThrowResourceNotFoundException() {
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(workspaceMemberRepository.existsByWorkspaceIdAndUserId(any(), any())).thenReturn(false);
        assertThrows(ResourceNotFoundException.class,
                () -> commentService.getComments(taskId, currentUser));
    }

    @Test
    void getComments_whenValid_shouldReturnCommentList() {
        Comment c1 = Comment.create(taskId, UUID.randomUUID(), "First");
        Comment c2 = Comment.create(taskId, UUID.randomUUID(), "Second");

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(workspaceMemberRepository.existsByWorkspaceIdAndUserId(any(), any())).thenReturn(true);
        when(commentRepository.findAllByTaskId(taskId)).thenReturn(List.of(c1, c2));

        List<CommentResponse> result = commentService.getComments(taskId, currentUser);

        assertEquals(2, result.size());
        assertEquals("First", result.get(0).getContent());
        assertEquals("Second", result.get(1).getContent());
    }
}
