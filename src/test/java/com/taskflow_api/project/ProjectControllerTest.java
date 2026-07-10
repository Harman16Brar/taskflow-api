package com.taskflow_api.project;

import com.jayway.jsonpath.JsonPath;
import com.taskflow_api.auth.RefreshTokenRepository;
import com.taskflow_api.shared.BaseIntegrationTest;
import com.taskflow_api.user.UserRepository;
import com.taskflow_api.workspace.WorkspaceMemberRepository;
import com.taskflow_api.workspace.WorkspaceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ProjectControllerTest extends BaseIntegrationTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private WorkspaceMemberRepository workspaceMemberRepository;
    @Autowired
    private WorkspaceRepository workspaceRepository;
    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private com.taskflow_api.task.TaskRepository taskRepository;
    @Autowired
    private com.taskflow_api.activity.ActivityLogRepository activityLogRepository;
    @Autowired
    private com.taskflow_api.comment.CommentRepository commentRepository;

    private String token;
    private String workspaceId;
    private String projectId;

    @BeforeEach
    void setUp() throws Exception {
        refreshTokenRepository.deleteAll();
        projectRepository.deleteAllHard(); // bypasses @SQLRestriction
        workspaceMemberRepository.deleteAll();
        workspaceRepository.deleteAll();
        userRepository.deleteAll();
        activityLogRepository.deleteAll();
        commentRepository.deleteAll();
        taskRepository.deleteAllHard();


        String email = uniqueEmail("project");
        token = registerAndGetToken(email, "Harman");

        // Get auto-created workspace
        var wsResult = mockMvc.perform(get("/api/v1/workspaces/")
                        .header("Authorization", "Bearer " + token))
                .andReturn();
        workspaceId = JsonPath.read(wsResult.getResponse().getContentAsString(), "$.data[0].workspaceId");

        // Create a project for use in tests
        var projResult = mockMvc.perform(post("/api/v1/workspaces/" + workspaceId + "/projects")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Test Project","description":"Test desc"}
                                """))
                .andReturn();
        projectId = JsonPath.read(projResult.getResponse().getContentAsString(), "$.data.id");
    }

    // ── POST /api/v1/workspaces/{workspaceId}/projects ────────────────────────

    @Test
    void createProject_withoutAuth_shouldReturn401() throws Exception {
        mockMvc.perform(post("/api/v1/workspaces/" + workspaceId + "/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Project","description":"desc"}
                                """))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void createProject_asOwner_shouldReturn201() throws Exception {
        mockMvc.perform(post("/api/v1/workspaces/" + workspaceId + "/projects")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"New Project","description":"desc"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.name").value("New Project"))
                .andExpect(jsonPath("$.data.id").exists());
    }

    @Test
    void createProject_withMissingName_shouldReturn400() throws Exception {
        mockMvc.perform(post("/api/v1/workspaces/" + workspaceId + "/projects")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"","description":"desc"}
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createProject_nonMember_shouldReturn404() throws Exception {
        String otherToken = registerAndGetToken(uniqueEmail("other"), "Other");
        mockMvc.perform(post("/api/v1/workspaces/" + workspaceId + "/projects")
                        .header("Authorization", "Bearer " + otherToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Project","description":"desc"}
                                """))
                .andExpect(status().isNotFound());
    }

    // ── GET /api/v1/workspaces/{workspaceId}/projects ─────────────────────────

    @Test
    void fetchAllProjects_asOwner_shouldReturn200WithList() throws Exception {
        mockMvc.perform(get("/api/v1/workspaces/" + workspaceId + "/projects")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].id").exists());
    }

    // ── GET /api/v1/workspaces/{workspaceId}/projects/{projectId} ─────────────

    @Test
    void fetchProjectById_shouldReturn200() throws Exception {
        mockMvc.perform(get("/api/v1/workspaces/" + workspaceId + "/projects/" + projectId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(projectId))
                .andExpect(jsonPath("$.data.name").value("Test Project"));
    }

    @Test
    void fetchProjectById_withInvalidId_shouldReturn404() throws Exception {
        mockMvc.perform(get("/api/v1/workspaces/" + workspaceId + "/projects/" + UUID.randomUUID())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }

    // ── PUT /api/v1/workspaces/{workspaceId}/projects/{projectId} ─────────────

    @Test
    void updateProject_asOwner_shouldReturn200() throws Exception {
        mockMvc.perform(put("/api/v1/workspaces/" + workspaceId + "/projects/" + projectId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Updated Project","description":"Updated desc","status":"ACTIVE"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("Updated Project"))
                .andExpect(jsonPath("$.data.status").value("ACTIVE"));
    }

    // ── DELETE /api/v1/workspaces/{workspaceId}/projects/{projectId} ──────────

    @Test
    void deleteProject_asOwner_shouldReturn200() throws Exception {
        // Create a separate project to delete
        var result = mockMvc.perform(post("/api/v1/workspaces/" + workspaceId + "/projects")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"To Delete","description":"desc"}
                                """))
                .andReturn();
        String pid = JsonPath.read(result.getResponse().getContentAsString(), "$.data.id");

        mockMvc.perform(delete("/api/v1/workspaces/" + workspaceId + "/projects/" + pid)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        // Verify it no longer shows in the list
        mockMvc.perform(get("/api/v1/workspaces/" + workspaceId + "/projects/" + pid)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteProject_nonOwner_shouldReturn403() throws Exception {
        // Register a second user and add them as MEMBER (can't do that via API yet; test with non-member)
        String otherToken = registerAndGetToken(uniqueEmail("nonowner"), "Other");
        mockMvc.perform(delete("/api/v1/workspaces/" + workspaceId + "/projects/" + projectId)
                        .header("Authorization", "Bearer " + otherToken))
                .andExpect(status().isNotFound()); // non-member gets 404
    }
}
