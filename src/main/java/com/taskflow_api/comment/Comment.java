package com.taskflow_api.comment;

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
@Table(name = "comments")
public class Comment extends BaseEntity {

    @Column(nullable = false)
    private UUID taskId;

    @Column(nullable = false)
    private UUID authorId;

    @Column(nullable = false)
    private String content;

    public static Comment create(UUID taskId, UUID authorId, String content) {
        Comment comment = new Comment();
        comment.setTaskId(taskId);
        comment.setAuthorId(authorId);
        comment.setContent(content);
        return comment;
    }

}
