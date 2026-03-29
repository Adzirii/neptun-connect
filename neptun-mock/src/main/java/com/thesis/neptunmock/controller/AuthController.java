package com.thesis.neptunmock.controller;

import com.thesis.neptunmock.dto.auth.LoginRequest;
import com.thesis.neptunmock.dto.auth.LoginResponse;
import com.thesis.neptunmock.dto.student.StudentDto;
import com.thesis.neptunmock.service.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;


/**
 * AuthController.java
 * REST Controller for authentication endpoints
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(
    name = "Authentication",
    description = "Authentication management APIs"
)
public class AuthController {

    private final AuthenticationService authenticationService;

    @Operation(
        summary = "Login",
        description = "Authenticate user with Neptun credentials and receive JWT token"
    )
    @ApiResponses(
        value = {
            @ApiResponse(
                responseCode = "200",
                description = "Login successful",
                content = @Content(schema = @Schema(implementation = LoginResponse.class))
            ),
            @ApiResponse(
                responseCode = "401",
                description = "Invalid credentials"
            ),
            @ApiResponse(
                responseCode = "400",
                description = "Invalid request format"
            )
        }
    )
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        log.info("Login request received for neptun code: {}", loginRequest.getNeptunCode());
        LoginResponse response = authenticationService.login(loginRequest);
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "Get current user profile",
        description = "Get the authenticated user's profile information"
    )
    @ApiResponses(
        value = {
            @ApiResponse(
                responseCode = "200",
                description = "Profile retrieved successfully",
                content = @Content(schema = @Schema(implementation = StudentDto.class))
            ),
            @ApiResponse(
                responseCode = "401",
                description = "Unauthorized - Invalid or missing token"
            )
        }
    )
    @GetMapping("/profile")
    public ResponseEntity<StudentDto> getProfile(
        @AuthenticationPrincipal UserDetails userDetails,
        @RequestHeader("Authorization") String authorizationHeader
    ) {
        log.info("Profile request for user: {}", userDetails.getUsername());

        String token = authorizationHeader.substring(7);
        StudentDto profile = authenticationService.validateToken(token);

        return ResponseEntity.ok(profile);
    }

    @Operation(
        summary = "Validate token",
        description = "Validate JWT token and return user information"
    )
    @ApiResponses(
        value = {
            @ApiResponse(
                responseCode = "200",
                description = "Token is valid",
                content = @Content(schema = @Schema(implementation = StudentDto.class))
            ),
            @ApiResponse(
                responseCode = "401",
                description = "Invalid token"
            )
        }
    )
    @GetMapping("/validate")
    public ResponseEntity<StudentDto> validateToken(
        @RequestHeader("Authorization") String authorizationHeader
    ) {
        String token = authorizationHeader.substring(7);
        StudentDto student = authenticationService.validateToken(token);
        return ResponseEntity.ok(student);
    }

}
