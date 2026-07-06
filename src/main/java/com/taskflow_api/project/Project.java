package com.taskflow_api.project;

import com.taskflow_api.shared.entity.BaseEntity;
import jakarta.persistence.*;
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
@Table(name = "projects")
public class Project extends BaseEntity {
    @Column(nullable = false)
    private String name;

    private String description;

    @Column(nullable = false)
    private UUID workspaceId;

    @Column(nullable = false)
    private UUID createdBy;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ProjectStatus status = ProjectStatus.PLANNING;

    public static Project create(String name, String description, UUID workspaceId, UUID createdBy) {
        Project newProject = new Project();
        newProject.name = name;
        newProject.description = description;
        newProject.workspaceId = workspaceId;
        newProject.createdBy = createdBy;
        return newProject;
    }
}
