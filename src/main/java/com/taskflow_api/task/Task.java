package com.taskflow_api.task;

import com.taskflow_api.shared.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@SQLRestriction("deleted = false")
@Table(name = "tasks")
public class Task extends BaseEntity {

    @Column(nullable = false)
    private String name;

    private String description;

    @Column(nullable = false)
    private UUID projectId;

    private UUID assigneeId;

    private UUID createdBy;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TaskStatus status = TaskStatus.TODO;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TaskPriority priority = TaskPriority.MEDIUM;

    private LocalDateTime dueDate;


    public static Task create(String name, String description, UUID projectId, UUID assigneeId, UUID createdBy, TaskPriority priority) {
        Task task = new Task();
        task.setName(name);
        task.setDescription(description);
        task.setProjectId(projectId);
        task.setAssigneeId(assigneeId);
        task.setPriority(priority);
        task.setCreatedBy(createdBy);

        return task;
    }
}
