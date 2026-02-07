package com.thesis.neptunmock.dto.auth;

import com.thesis.neptunmock.dto.student.StudentDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponse {

    private String token;
    private String tokenType;
    private Long expiresIn;
    private StudentDto student;

    public LoginResponse(String token, Long expiresIn, StudentDto student) {
        this.token = token;
        this.tokenType = "Bearer";
        this.expiresIn = expiresIn;
        this.student = student;
    }
}
