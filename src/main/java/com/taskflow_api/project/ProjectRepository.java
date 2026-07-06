package com.taskflow_api.project;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProjectRepository extends JpaRepository<Project, UUID> {
    List<Project> findAllByWorkspaceId(UUID workspaceId);

    Optional<Project> findByWorkspaceIdAndId(UUID workspaceId, UUID projectId);

    boolean existsByWorkspaceIdAndId(UUID workspaceId, UUID projectId);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM projects", nativeQuery = true)
    void deleteAllHard();
}
