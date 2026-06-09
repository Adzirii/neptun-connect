package com.thesis.chatservice.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.boot.test.mock.mockito.MockBean;
import com.thesis.chatservice.security.JwtUtil;
import com.thesis.chatservice.service.UserService;
import com.thesis.chatservice.entity.User;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private UserService userService;

    @Test
    @WithMockUser
    public void getCurrentUserProfile_ReturnsOk() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setNeptunCode("NEPTUN");


        mockMvc.perform(get("/api/auth/profile")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}

