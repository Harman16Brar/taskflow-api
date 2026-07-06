package com.taskflow_api.shared.scheduler;

import com.taskflow_api.shared.email.EmailService;
import com.taskflow_api.task.Task;
import com.taskflow_api.task.TaskRepository;
import com.taskflow_api.task.TaskStatus;
import com.taskflow_api.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
@RequiredArgsConstructor
@ConditionalOnProperty(name = "scheduling.enabled", havingValue = "true", matchIfMissing = true)
public class ScheduledJobs {
    private final TaskRepository taskRepository;
    private final EmailService emailService;
    private final UserRepository userRepository;

    @Scheduled(cron = "0 0 0 * * *")
    public void detectOverdueTasks() {
        List<Task> overdueTasks = taskRepository.findOverdueTasks(LocalDateTime.now(), List.of(TaskStatus.DONE, TaskStatus.CANCELLED));

        if (overdueTasks.isEmpty()) {
            log.info("No overdue tasks found.");
            return;
        }

        log.warn("Found {} overdue task(s):", overdueTasks.size());

        // Group by assigneeId, skip unassigned tasks
        overdueTasks.stream()
                .filter(task -> task.getAssigneeId() != null)
                .collect(Collectors.groupingBy(Task::getAssigneeId))
                .forEach((assigneeId, tasks) ->
                        userRepository.findById(assigneeId)
                                .ifPresent(user -> emailService.sendOverdueTasksEmail(
                                        user.getEmail(),
                                        tasks
                                ))
                );
    }
}
