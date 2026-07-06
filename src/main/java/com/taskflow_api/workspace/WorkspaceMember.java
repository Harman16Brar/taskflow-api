package com.taskflow_api.workspace;

import com.taskflow_api.shared.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "workspace_members")
@Getter
@Setter
@NoArgsConstructor
public class WorkspaceMember extends BaseEntity {
    @Column(nullable = false)
    private UUID workspaceId;

    @Column(nullable = false)
    private UUID userId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private WorkspaceRole role;

    @Column(nullable = false)
    private LocalDateTime joinedAt = LocalDateTime.now();

    public static WorkspaceMember create(UUID workspaceId, UUID userId, WorkspaceRole role) {
        WorkspaceMember workspaceMember = new WorkspaceMember();
        workspaceMember.setWorkspaceId(workspaceId);
        workspaceMember.setUserId(userId);
        workspaceMember.setRole(role);
        return workspaceMember;
    }
}
