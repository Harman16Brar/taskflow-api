package com.taskflow_api.workspace;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WorkspaceMemberRepository extends JpaRepository<WorkspaceMember, UUID> {
    boolean existsByWorkspaceIdAndUserId(UUID workspaceId, UUID userId);

    Optional<WorkspaceMember> findByWorkspaceIdAndUserId(UUID workspaceId, UUID id);

}
