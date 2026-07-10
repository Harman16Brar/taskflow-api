package com.taskflow_api;

import com.taskflow_api.auth.RefreshTokenRepository;
import com.taskflow_api.shared.BaseIntegrationTest;
import com.taskflow_api.user.UserRepository;
import com.taskflow_api.workspace.WorkspaceMember;
import com.taskflow_api.workspace.WorkspaceMemberRepository;
import com.taskflow_api.workspace.WorkspaceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class AuthControllerTest extends BaseIntegrationTest {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WorkspaceRepository workspaceRepository;

    @Autowired
    private WorkspaceMemberRepository workspaceMemberRepository;
    @Autowired
    private RefreshTokenRepository refreshTokenRepository;
    @Autowired
    private com.taskflow_api.project.ProjectRepository projectRepository;
    @Autowired
    private com.taskflow_api.task.TaskRepository taskRepository;
    @Autowired
    private com.taskflow_api.activity.ActivityLogRepository activityLogRepository;
    @Autowired
    private com.taskflow_api.comment.CommentRepository commentRepository;

    @BeforeEach
    void setUp() {
        refreshTokenRepository.deleteAll();
        activityLogRepository.deleteAll();
        commentRepository.deleteAll();
        taskRepository.deleteAllHard();
        projectRepository.deleteAllHard();  // bypasses @SQLRestriction
        workspaceMemberRepository.deleteAll();
        workspaceRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void register_shouldCreateUserAndReturnToken() throws Exception {
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                "email": "test@test.com",
                                "password": "password123",
                                "firstName": "Test",
                                "lastName": "User"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.accessToken").exists())
                .andExpect(jsonPath("$.data.accessToken").isNotEmpty());
    }

    @Test
    void register_withDuplicateEmail_shouldReturn409() throws Exception {
        //register once
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "test@test.com",
                                  "password": "password123",
                                  "firstName": "Test",
                                  "lastName": "User"
                                }
                                """))
                .andExpect(status().isCreated());

        // register again with same email
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "test@test.com",
                                  "password": "password123",
                                  "firstName": "Test",
                                  "lastName": "User"
                                }
                                """))
                .andExpect(status().isConflict());
    }

    @Test
    void login_withValidCredentials_shouldReturnToken() throws Exception {
        // register first
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "test@test.com",
                                  "password": "password123",
                                  "firstName": "Test",
                                  "lastName": "User"
                                }
                                """))
                .andExpect(status().isCreated());

        // then login
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "test@test.com",
                                  "password": "password123"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.accessToken").exists());
    }

    @Test
    void login_withWrongPassword_shouldReturn401() throws Exception {
        // register first
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "test@test.com",
                                  "password": "password123",
                                  "firstName": "Test",
                                  "lastName": "User"
                                }
                                """))
                .andExpect(status().isCreated());

        // login with wrong password
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "test@test.com",
                                  "password": "wrongpassword"
                                }
                                """))
                .andExpect(status().isUnauthorized());
    }
}
