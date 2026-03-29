package com.thesis.chatservice.controller;

import com.thesis.chatservice.dto.user.UserDto;
import com.thesis.chatservice.entity.User;
import com.thesis.chatservice.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(
    name = "Authentication",
    description = "Authentication management APIs"
)
public class AuthController {

    private final UserService userService;

    @Operation(
        summary = "Get current user profile",
        description = "Get the authenticated user's profile information",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @GetMapping("/profile")
    public ResponseEntity<UserDto> getCurrentUserProfile(
        @AuthenticationPrincipal User currentUser,
        @RequestHeader(
            value = "Authorization",
            required = false
        ) String authHeader
    ) {

        if (authHeader != null && authHeader.startsWith("Bearer ")) {

            String token = authHeader.substring(7);
            User syncedUser = userService.syncUserFromNeptun(currentUser.getNeptunCode(), token);
            return ResponseEntity.ok(mapToDto(syncedUser));
        }

        return ResponseEntity.ok(mapToDto(currentUser));
    }

    @Operation(
        summary = "Sync user with Neptun",
        description = "Synchronize user data with Neptun system",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @PostMapping("/sync")
    public ResponseEntity<UserDto> syncWithNeptun(
        @AuthenticationPrincipal User currentUser,
        @RequestHeader("Authorization") String authHeader
    ) {
        String token = authHeader.substring(7);
        User syncedUser = userService.syncUserFromNeptun(currentUser.getNeptunCode(), token);
        return ResponseEntity.ok(mapToDto(syncedUser));
    }

    private UserDto mapToDto(User user) {
        return UserDto.builder()
            .id(user.getId())
            .neptunCode(user.getNeptunCode())
            .name(user.getName())
            .email(user.getEmail())
            .program(user.getProgram())
            .faculty(user.getFaculty())
            .semester(user.getSemester())
            .status(user.getStatus())
            .avatarUrl(user.getAvatarUrl())
            .lastSeenAt(user.getLastSeenAt())
            .build();
    }

}
