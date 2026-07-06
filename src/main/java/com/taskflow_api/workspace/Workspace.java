package com.taskflow_api.workspace;

import com.taskflow_api.shared.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLRestriction;

import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@SQLRestriction("deleted = false")
@Table(name = "workspaces")
public class Workspace extends BaseEntity {
    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private UUID ownerId;

    public static Workspace create(String name, UUID ownerId) {
        Workspace workspace = new Workspace();
        workspace.setName(name);
        workspace.setOwnerId(ownerId);
        return workspace;
    }
}
