package com.taskflow_api.task;

import com.taskflow_api.activity.TaskAction;
import com.taskflow_api.activity.TaskEvent;
import com.taskflow_api.project.Project;
import com.taskflow_api.project.ProjectRepository;
import com.taskflow_api.shared.email.EmailService;
import com.taskflow_api.shared.exception.ResourceNotFoundException;
import com.taskflow_api.task.dto.CreateTaskRequest;
import com.taskflow_api.task.dto.TaskResponse;
import com.taskflow_api.task.dto.UpdateTaskRequest;
import com.taskflow_api.user.User;
import com.taskflow_api.user.UserRepository;
import com.taskflow_api.workspace.WorkspaceMember;
import com.taskflow_api.workspace.WorkspaceMemberRepository;
import com.taskflow_api.workspace.WorkspaceRole;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final WorkspaceMemberRepository workspaceMemberRepository;
    private final UserRepository userRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final EmailService emailService;

    public TaskResponse create(UUID projectId, CreateTaskRequest request, User currentUser) {
        //fetch project
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project", projectId.toString()));

        //get the workspaceId
        UUID workspaceId = project.getWorkspaceId();

        //Check if current user is a workspace member
        if (!workspaceMemberRepository.existsByWorkspaceIdAndUserId(workspaceId, currentUser.getId())) {
            throw new ResourceNotFoundException("Workspace", workspaceId.toString());
        }

        //Create and save the task
        Task task = Task.create(request.getName(),
                request.getDescription(),
                projectId,
                request.getAssigneeId(),
                currentUser.getId());

        Task savedTask = taskRepository.save(task);

        if (request.getAssigneeId() != null) {
            User assignee = userRepository.findById(savedTask.getAssigneeId())
                    .orElseThrow(() -> new ResourceNotFoundException("User", savedTask.getAssigneeId().toString()));

            emailService.sendTaskAssignedEmail(assignee.getEmail(), savedTask.getName(), project.getName(), savedTask.getPriority().name());
        }

        eventPublisher.publishEvent(new TaskEvent(this, savedTask, currentUser, TaskAction.TASK_CREATED, "Task created"));

        return TaskResponse.builder()
                .id(savedTask.getId())
                .name(savedTask.getName())
                .description(savedTask.getDescription())
                .projectId(savedTask.getProjectId())
                .assigneeId(savedTask.getAssigneeId())
                .createdBy(savedTask.getCreatedBy())
                .status(savedTask.getStatus().toString())
                .priority(savedTask.getPriority().toString())
                .dueDate(savedTask.getDueDate())
                .createdAt(savedTask.getCreatedAt())
                .build();
    }

    public Page<TaskResponse> fetchAllTasks(UUID projectId,
                                            User currentUser,
                                            Pageable pageable,
                                            TaskStatus status,
                                            TaskPriority priority,
                                            UUID assigneeId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project", projectId.toString()));

        if (!workspaceMemberRepository.existsByWorkspaceIdAndUserId(project.getWorkspaceId(), currentUser.getId())) {
            throw new ResourceNotFoundException("Project", projectId.toString());
        }

        // build specification dynamically
        Specification<Task> spec = TaskSpecification.hasProjectId(projectId);

        if (status != null) spec = spec.and(TaskSpecification.hasStatus(status));
        if (priority != null) spec = spec.and(TaskSpecification.hasPriority(priority));
        if (assigneeId != null) spec = spec.and(TaskSpecification.hasAssigneeId(assigneeId));


        return taskRepository.findAll(spec, pageable)
                .map(this::toResponse);
//        return taskRepository.findAllByProjectId(projectId, pageable)
//                .map(this::toResponse);

//        return taskRepository.findAllByProjectId(projectId, pageable)
//                .stream()
//                .map(this::toResponse)
//                .toList();
    }

    public TaskResponse fetchTaskById(UUID projectId, UUID taskId, User currentUser) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project", projectId.toString()));

        if (!workspaceMemberRepository.existsByWorkspaceIdAndUserId(project.getWorkspaceId(), currentUser.getId())) {
            throw new ResourceNotFoundException("Project", projectId.toString());
        }

        Task task = taskRepository.findByProjectIdAndId(projectId, taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task", taskId.toString()));

        return toResponse(task);
    }

    public TaskResponse updateTask(UUID projectId, UUID taskId, UpdateTaskRequest request, User currentUser) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project", projectId.toString()));

        if (!workspaceMemberRepository.existsByWorkspaceIdAndUserId(project.getWorkspaceId(), currentUser.getId())) {
            throw new ResourceNotFoundException("Project", projectId.toString());
        }

        Task task = taskRepository.findByProjectIdAndId(projectId, taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task", taskId.toString()));

        // build detail string
        String detail = buildUpdateDetail(task, request);


        if (request.getName() != null) task.setName(request.getName());
        if (request.getDescription() != null) task.setDescription(request.getDescription());
        if (request.getStatus() != null) task.setStatus(request.getStatus());
        if (request.getPriority() != null) task.setPriority(request.getPriority());
        if (request.getAssigneeId() != null) task.setAssigneeId(request.getAssigneeId());
        if (request.getDueDate() != null) task.setDueDate(request.getDueDate());

        Task savedTask = taskRepository.save(task);

        if (request.getAssigneeId() != null
                && !request.getAssigneeId().equals(savedTask.getAssigneeId())) {

            User assignee = userRepository.findById(request.getAssigneeId())
                    .orElseThrow(() -> new ResourceNotFoundException("User", request.getAssigneeId().toString()));

            emailService.sendTaskAssignedEmail(
                    assignee.getEmail(),
                    savedTask.getName(),
                    project.getName(),
                    savedTask.getPriority().name()
            );
        }

        eventPublisher.publishEvent(new TaskEvent(this, task, currentUser, TaskAction.TASK_UPDATED, detail));

        return toResponse(savedTask);
    }

    public void deleteTask(UUID projectId, UUID taskId, User currentUser) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project", projectId.toString()));

        WorkspaceMember member = workspaceMemberRepository
                .findByWorkspaceIdAndUserId(project.getWorkspaceId(), currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Project", projectId.toString()));

        if (member.getRole() != WorkspaceRole.OWNER && member.getRole() != WorkspaceRole.ADMIN) {
            throw new ResourceNotFoundException("Task", taskId.toString());
        }

        Task task = taskRepository.findByProjectIdAndId(projectId, taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task", taskId.toString()));

        //taskRepository.deleteById(task.getId());
        task.setDeleted(true);
        task.setDeletedAt(LocalDateTime.now());
        taskRepository.save(task);
        eventPublisher.publishEvent(new TaskEvent(this, task, currentUser, TaskAction.TASK_DELETED, "Task deleted"));
    }

    private String buildUpdateDetail(Task task, UpdateTaskRequest request) {
        List<String> changes = new ArrayList<>();

        if (request.getStatus() != null && request.getStatus() != task.getStatus()) {
            changes.add("status changed from " + task.getStatus().name() + " to " + request.getStatus().name());
        }

        if (request.getPriority() != null && request.getPriority() != task.getPriority()) {
            changes.add("priority changed from " + task.getPriority().name() + " to " + request.getPriority().name());
        }

        if (request.getName() != null && !request.getName().equals(task.getName())) {
            changes.add("name updated");
        }

        if (request.getDescription() != null && !request.getDescription().equals(task.getDescription())) {
            changes.add("description updated");
        }

        if (request.getAssigneeId() != null && !request.getAssigneeId().equals(task.getAssigneeId())) {
            changes.add("assignee updated");
        }

        if (request.getDueDate() != null && !request.getDueDate().equals(task.getDueDate())) {
            changes.add("due date updated");
        }

        if (changes.isEmpty()) {
            return "no changes detected";
        }

        return String.join(", ", changes);
    }

    private TaskResponse toResponse(Task task) {
        return TaskResponse.builder()
                .id(task.getId())
                .name(task.getName())
                .description(task.getDescription())
                .projectId(task.getProjectId())
                .assigneeId(task.getAssigneeId())
                .createdBy(task.getCreatedBy())
                .status(task.getStatus().toString())
                .priority(task.getPriority().toString())
                .dueDate(task.getDueDate())
                .createdAt(task.getCreatedAt())
                .build();
    }
}
