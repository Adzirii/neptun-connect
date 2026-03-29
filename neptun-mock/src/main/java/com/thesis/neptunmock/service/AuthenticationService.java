package com.thesis.neptunmock.service;

import com.thesis.neptunmock.dto.auth.LoginRequest;
import com.thesis.neptunmock.dto.auth.LoginResponse;
import com.thesis.neptunmock.dto.student.StudentDto;
import com.thesis.neptunmock.model.Student;
import com.thesis.neptunmock.repository.MockDataRepository;
import com.thesis.neptunmock.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final MockDataRepository repository;

    public LoginResponse login(LoginRequest loginRequest) {
        log.info("Attempting login for neptun code: {}", loginRequest.getNeptunCode());

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getNeptunCode(),
                            loginRequest.getPassword()
                    )
            );

            Student student = repository.findStudentByNeptunCode(loginRequest.getNeptunCode())
                .orElseThrow(() -> new RuntimeException("Student not found"));

            String token = jwtUtil.generateToken(student.getNeptunCode(), student.getName());

            StudentDto studentDto = StudentDto.builder()
                .id(student.getId())
                .neptunCode(student.getNeptunCode())
                .name(student.getName())
                .email(student.getEmail())
                .program(student.getProgram())
                .faculty(student.getFaculty())
                .semester(student.getSemester())
                .enrollmentDate(student.getEnrollmentDate())
                .status(student.getStatus())
                .build();

            log.info("Login successful for user: {}", loginRequest.getNeptunCode());

            return new LoginResponse(
                token,
                jwtUtil.getExpirationTime(),
                studentDto
            );

        } catch (AuthenticationException e) {
            log.error("Authentication failed for user: {}", loginRequest.getNeptunCode());
            throw new RuntimeException("Invalid neptun code or password");
        }
    }

    public StudentDto validateToken(String token) {
        String neptunCode = jwtUtil.extractNeptunCode(token);

        if (jwtUtil.validateToken(token, neptunCode)) {
            Student student = repository.findStudentByNeptunCode(neptunCode)
                .orElseThrow(() -> new RuntimeException("Student not found"));

            return StudentDto.builder()
                .id(student.getId())
                .neptunCode(student.getNeptunCode())
                .name(student.getName())
                .email(student.getEmail())
                .program(student.getProgram())
                .faculty(student.getFaculty())
                .semester(student.getSemester())
                .enrollmentDate(student.getEnrollmentDate())
                .status(student.getStatus())
                .build();
        }

        throw new RuntimeException("Invalid token");
    }

}
