package com.taskflow_api.workspace.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@Jacksonized
public class WorkspaceResponse implements Serializable {

    private final UUID workspaceId;
    private final String workspaceName;
    private final String ownerName;
    private final LocalDateTime createdAt;


}
