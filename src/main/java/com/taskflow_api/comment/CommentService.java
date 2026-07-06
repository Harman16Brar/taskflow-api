package com.taskflow_api.comment;

import com.taskflow_api.comment.dto.CommentResponse;
import com.taskflow_api.comment.dto.CreateCommentRequest;
import com.taskflow_api.project.Project;
import com.taskflow_api.project.ProjectRepository;
import com.taskflow_api.shared.exception.ResourceNotFoundException;
import com.taskflow_api.shared.exception.UnauthorizedAccessException;
import com.taskflow_api.task.TaskRepository;
import com.taskflow_api.user.User;
import com.taskflow_api.workspace.WorkspaceMember;
import com.taskflow_api.workspace.WorkspaceMemberRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class CommentService {
    private final CommentRepository commentRepository;
    private final TaskRepository taskRepository;
    private final WorkspaceMemberRepository workspaceMemberRepository;
    private final ProjectRepository projectRepository;

    public CommentService(CommentRepository commentRepository, TaskRepository taskRepository, WorkspaceMemberRepository workspaceMemberRepository, ProjectRepository projectRepository) {
        this.commentRepository = commentRepository;
        this.taskRepository = taskRepository;
        this.workspaceMemberRepository = workspaceMemberRepository;
        this.projectRepository = projectRepository;
    }


    public CommentResponse addComment(UUID taskId, CreateCommentRequest request, User currentUser) {
        //Verify task exists
        var task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task", taskId.toString()));

        //Verify current user is a workspace member
        // (get projectId from task → get workspaceId from project)
        UUID projectId = task.getProjectId();
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project", projectId.toString()));

        if (!workspaceMemberRepository.existsByWorkspaceIdAndUserId(project.getWorkspaceId(), currentUser.getId())) {
            throw new ResourceNotFoundException("Workspace", project.getWorkspaceId().toString());
        }

        //Create and save comment
        Comment comment = Comment.create(taskId, currentUser.getId(), request.getContent());
        Comment savedComment = commentRepository.save(comment);

        return toResponse(savedComment);
    }

    public List<CommentResponse> getComments(UUID taskId, User currentUser) {

        //Verify task exists
        var task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task", taskId.toString()));

        //Verify current user is a workspace member
        // (get projectId from task → get workspaceId from project)
        UUID projectId = task.getProjectId();
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project", projectId.toString()));

        if (!workspaceMemberRepository.existsByWorkspaceIdAndUserId(project.getWorkspaceId(), currentUser.getId())) {
            throw new ResourceNotFoundException("Workspace", project.getWorkspaceId().toString());
        }

        List<Comment> comments = commentRepository.findAllByTaskId(taskId);

        return comments.stream()
                .map(comment -> {
                    return toResponse(comment);
                }).toList();

    }

    private CommentResponse toResponse(Comment comment) {
        return CommentResponse.builder()
                .id(comment.getId())
                .taskId(comment.getTaskId())
                .authorId(comment.getAuthorId())
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .build();
    }
}
