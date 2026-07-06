package com.taskflow_api.activity;

import com.taskflow_api.activity.dto.ActivityLogResponse;
import com.taskflow_api.project.ProjectRepository;
import com.taskflow_api.shared.exception.ResourceNotFoundException;
import com.taskflow_api.task.TaskRepository;
import com.taskflow_api.user.User;
import com.taskflow_api.workspace.WorkspaceMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ActivityLogService {

    private final ActivityLogRepository activityLogRepository;
    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final WorkspaceMemberRepository workspaceMemberRepository;

    public List<ActivityLogResponse> getTaskActivity(UUID taskId, User currentUser) {
        var task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task", taskId.toString()));

        var project = projectRepository.findById(task.getProjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Project", task.getProjectId().toString()));

        if (!workspaceMemberRepository.existsByWorkspaceIdAndUserId(project.getWorkspaceId(), currentUser.getId())) {
            throw new ResourceNotFoundException("Task", taskId.toString());
        }

        return activityLogRepository.findAllByTaskIdOrderByCreatedAtDesc(taskId)
                .stream()
                .map(log -> ActivityLogResponse.builder()
                        .id(log.getId())
                        .taskId(log.getTaskId())
                        .userId(log.getUserId())
                        .action(log.getAction())
                        .detail(log.getDetail())
                        .createdAt(log.getCreatedAt())
                        .build())
                .toList();
    }
}