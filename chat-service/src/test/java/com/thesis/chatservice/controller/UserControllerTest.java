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
import com.thesis.chatservice.dto.user.UserDto;
import org.springframework.http.MediaType;

import java.util.Collections;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private UserService userService;

    @Test
    public void searchUsers_ReturnsOk() throws Exception {
        when(userService.searchUsers("test")).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/users/search")
                .param("query", "test")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}

