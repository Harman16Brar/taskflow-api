package com.taskflow_api.task;

import com.jayway.jsonpath.JsonPath;
import com.taskflow_api.activity.ActivityLogRepository;
import com.taskflow_api.auth.RefreshTokenRepository;
import com.taskflow_api.comment.CommentRepository;
import com.taskflow_api.project.ProjectRepository;
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

class TaskControllerTest extends BaseIntegrationTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private WorkspaceMemberRepository workspaceMemberRepository;
    @Autowired
    private WorkspaceRepository workspaceRepository;
    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private ActivityLogRepository activityLogRepository;
    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    private String token;
    private String projectId;
    private String taskId;

    @BeforeEach
    void setUp() throws Exception {
        refreshTokenRepository.deleteAll();
        activityLogRepository.deleteAll();
        commentRepository.deleteAll();
        taskRepository.deleteAllHard();
        projectRepository.deleteAllHard();
        workspaceMemberRepository.deleteAll();
        workspaceRepository.deleteAll();
        userRepository.deleteAll();

        String email = uniqueEmail("task");
        token = registerAndGetToken(email, "Harman");

        // Get auto-created workspace
        var wsResult = mockMvc.perform(get("/api/v1/workspaces/")
                        .header("Authorization", "Bearer " + token))
                .andReturn();
        String workspaceId = JsonPath.read(wsResult.getResponse().getContentAsString(), "$.data[0].workspaceId");

        // Create project
        var projResult = mockMvc.perform(post("/api/v1/workspaces/" + workspaceId + "/projects")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Test Project","description":"desc"}
                                """))
                .andReturn();
        projectId = JsonPath.read(projResult.getResponse().getContentAsString(), "$.data.id");

        // Create a task for use in tests
        var taskResult = mockMvc.perform(post("/api/v1/projects/" + projectId + "/tasks")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Test Task","description":"Task desc","priority":"MEDIUM"}
                                """))
                .andReturn();
        taskId = JsonPath.read(taskResult.getResponse().getContentAsString(), "$.data.id");
    }

    // ── POST /api/v1/projects/{projectId}/tasks ───────────────────────────────

    @Test
    void createTask_withoutAuth_shouldReturn401() throws Exception {
        mockMvc.perform(post("/api/v1/projects/" + projectId + "/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Task","priority":"MEDIUM"}
                                """))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void createTask_withAuth_shouldReturn201() throws Exception {
        mockMvc.perform(post("/api/v1/projects/" + projectId + "/tasks")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"New Task","description":"desc","priority":"HIGH"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.id").exists())
                .andExpect(jsonPath("$.data.name").value("New Task"))
                .andExpect(jsonPath("$.data.status").value("TODO"))
                .andExpect(jsonPath("$.data.priority").value("HIGH"));
    }

    @Test
    void createTask_withMissingName_shouldReturn400() throws Exception {
        mockMvc.perform(post("/api/v1/projects/" + projectId + "/tasks")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"description":"desc","priority":"MEDIUM"}
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createTask_forNonExistentProject_shouldReturn404() throws Exception {
        mockMvc.perform(post("/api/v1/projects/" + UUID.randomUUID() + "/tasks")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Task","priority":"MEDIUM"}
                                """))
                .andExpect(status().isNotFound());
    }

    // ── GET /api/v1/projects/{projectId}/tasks ────────────────────────────────

    @Test
    void fetchAllTasks_shouldReturn200WithPaginatedResults() throws Exception {
        mockMvc.perform(get("/api/v1/projects/" + projectId + "/tasks")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.totalElements").value(1));
    }

    @Test
    void fetchAllTasks_withStatusFilter_shouldReturnFilteredTasks() throws Exception {
        mockMvc.perform(get("/api/v1/projects/" + projectId + "/tasks")
                        .param("status", "TODO")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].status").value("TODO"));
    }

    @Test
    void fetchAllTasks_withStatusFilterNonMatching_shouldReturnEmpty() throws Exception {
        mockMvc.perform(get("/api/v1/projects/" + projectId + "/tasks")
                        .param("status", "DONE")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.totalElements").value(0));
    }

    // ── GET /api/v1/projects/{projectId}/tasks/{taskId} ───────────────────────

    @Test
    void fetchTaskById_shouldReturn200() throws Exception {
        mockMvc.perform(get("/api/v1/projects/" + projectId + "/tasks/" + taskId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(taskId))
                .andExpect(jsonPath("$.data.name").value("Test Task"));
    }

    @Test
    void fetchTaskById_withInvalidId_shouldReturn404() throws Exception {
        mockMvc.perform(get("/api/v1/projects/" + projectId + "/tasks/" + UUID.randomUUID())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }

    // ── PUT /api/v1/projects/{projectId}/tasks/{taskId} ───────────────────────

    @Test
    void updateTask_shouldReturn200WithUpdatedData() throws Exception {
        mockMvc.perform(put("/api/v1/projects/" + projectId + "/tasks/" + taskId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Updated Task","status":"IN_PROGRESS","priority":"HIGH"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("Updated Task"))
                .andExpect(jsonPath("$.data.status").value("IN_PROGRESS"))
                .andExpect(jsonPath("$.data.priority").value("HIGH"));
    }

    @Test
    void updateTask_withInvalidId_shouldReturn404() throws Exception {
        mockMvc.perform(put("/api/v1/projects/" + projectId + "/tasks/" + UUID.randomUUID())
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Updated"}
                                """))
                .andExpect(status().isNotFound());
    }

    // ── DELETE /api/v1/projects/{projectId}/tasks/{taskId} ────────────────────

    @Test
    void deleteTask_asOwner_shouldReturn200() throws Exception {
        // Create a task to delete
        var result = mockMvc.perform(post("/api/v1/projects/" + projectId + "/tasks")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"To Delete","priority":"MEDIUM"}
                                """))
                .andReturn();
        String tid = JsonPath.read(result.getResponse().getContentAsString(), "$.data.id");

        mockMvc.perform(delete("/api/v1/projects/" + projectId + "/tasks/" + tid)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        // Verify deleted task is not found
        mockMvc.perform(get("/api/v1/projects/" + projectId + "/tasks/" + tid)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteTask_byNonMember_shouldReturn404() throws Exception {
        String otherToken = registerAndGetToken(uniqueEmail("nonmember"), "Other");
        mockMvc.perform(delete("/api/v1/projects/" + projectId + "/tasks/" + taskId)
                        .header("Authorization", "Bearer " + otherToken))
                .andExpect(status().isNotFound());
    }
}
