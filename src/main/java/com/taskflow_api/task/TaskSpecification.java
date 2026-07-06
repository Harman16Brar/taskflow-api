package com.taskflow_api.task;

import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

public class TaskSpecification {
    public static Specification<Task> hasProjectId(UUID projectId) {
        return (root, query, cb) -> cb.equal(root.get("projectId"), projectId);
    }

    public static Specification<Task> hasStatus(TaskStatus status) {
        return (root, query, cb) -> cb.equal(root.get("status"), status);
    }

    public static Specification<Task> hasPriority(TaskPriority priority) {
        return (root, query, cb) -> cb.equal(root.get("priority"), priority);
    }

    public static Specification<Task> hasAssigneeId(UUID assigneeId) {
        return (root, query, cb) -> cb.equal(root.get("assigneeId"), assigneeId);
    }
}
