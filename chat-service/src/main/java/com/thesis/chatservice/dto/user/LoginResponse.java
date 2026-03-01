package com.thesis.chatservice.dto.user;


import com.thesis.chatservice.client.dto.NeptunStudentDto;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    private String token;
    private String tokenType;
    private Long expiresIn;
    private NeptunStudentDto student;
}
