package com.taskflow_api.workspace;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface WorkspaceRepository extends JpaRepository<Workspace, UUID> {
    @Query("SELECT w FROM Workspace w JOIN WorkspaceMember wm ON w.id = wm.workspaceId WHERE wm.userId = :userId")
    List<Workspace> findAllByMemberUserId(@Param("userId") UUID userId);
}
