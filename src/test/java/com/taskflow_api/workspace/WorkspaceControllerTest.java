package com.taskflow_api.workspace;

import com.jayway.jsonpath.JsonPath;
import com.taskflow_api.shared.BaseIntegrationTest;
import com.taskflow_api.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class WorkspaceControllerTest extends BaseIntegrationTest {

    @Autowired private UserRepository userRepository;
    @Autowired private WorkspaceMemberRepository workspaceMemberRepository;
    @Autowired private WorkspaceRepository workspaceRepository;

    private String token;
    private String workspaceId;

    @BeforeEach
    void setUp() throws Exception {
        workspaceMemberRepository.deleteAll();
        workspaceRepository.deleteAll();
        userRepository.deleteAll();

        String email = uniqueEmail("workspace");
        token = registerAndGetToken(email, "Harman");

        // Get the auto-created default workspace
        var result = mockMvc.perform(get("/api/v1/workspaces/")
                        .header("Authorization", "Bearer " + token))
                .andReturn();
        workspaceId = JsonPath.read(result.getResponse().getContentAsString(), "$.data[0].workspaceId");
    }

    // ── GET /api/v1/workspaces/ ───────────────────────────────────────────────

    @Test
    void getUserWorkspaces_withoutAuth_shouldReturn401() throws Exception {
        mockMvc.perform(get("/api/v1/workspaces/"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getUserWorkspaces_withAuth_shouldReturnWorkspaceList() throws Exception {
        mockMvc.perform(get("/api/v1/workspaces/")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].workspaceId").exists());
    }

    // ── POST /api/v1/workspaces/ ──────────────────────────────────────────────

    @Test
    void createWorkspace_withoutAuth_shouldReturn401() throws Exception {
        mockMvc.perform(post("/api/v1/workspaces/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"New Workspace"}
                                """))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void createWorkspace_withAuth_shouldReturn201() throws Exception {
        mockMvc.perform(post("/api/v1/workspaces/")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"New Workspace"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.workspaceId").exists())
                .andExpect(jsonPath("$.data.workspaceName").value("New Workspace"));
    }

    @Test
    void createWorkspace_withEmptyName_shouldReturn400() throws Exception {
        mockMvc.perform(post("/api/v1/workspaces/")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":""}
                                """))
                .andExpect(status().isBadRequest());
    }

    // ── GET /api/v1/workspaces/{id} ───────────────────────────────────────────

    @Test
    void getWorkspace_withAuth_shouldReturn200() throws Exception {
        mockMvc.perform(get("/api/v1/workspaces/" + workspaceId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.workspaceId").value(workspaceId));
    }

    @Test
    void getWorkspace_withNonMember_shouldReturn404() throws Exception {
        // register a second user who is not in the workspace
        String otherToken = registerAndGetToken(uniqueEmail("nonmember"), "Other");

        mockMvc.perform(get("/api/v1/workspaces/" + workspaceId)
                        .header("Authorization", "Bearer " + otherToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void getWorkspace_withInvalidId_shouldReturn404() throws Exception {
        mockMvc.perform(get("/api/v1/workspaces/" + UUID.randomUUID())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }
}
