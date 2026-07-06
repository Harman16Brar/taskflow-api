package com.taskflow_api.task;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TaskRepository extends JpaRepository<Task, UUID>, JpaSpecificationExecutor<Task> {

    //List<Task> findAllByProjectId(UUID projectId);

    Page<Task> findAllByProjectId(UUID projectId, Pageable pageable);

    Optional<Task> findByProjectIdAndId(UUID projectId, UUID id);

    @Query("SELECT t FROM Task t WHERE t.dueDate <:now AND t.status NOT IN :excludedStatuses")
    List<Task> findOverdueTasks(@Param("now") LocalDateTime now,
                                @Param("excludedStatuses") List<TaskStatus> excludedStatuses);
    //findByDueDateBeforeAndStatusNotIn(LocalDateTime now, List<TaskStatus> statuses)
}
