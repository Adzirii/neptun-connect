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

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Users", description = "User management APIs")
@SecurityRequirement(name = "Bearer Authentication")
public class UserController {

    private final UserService userService;

    @Operation(summary = "Get user by ID", description = "Get user information by user ID")
    @GetMapping("/{userId}")
    public ResponseEntity<UserDto> getUserById(@PathVariable("userId") Long userId) {
        log.info("Request for user: {}", userId);
        User user = userService.getUserById(userId);
        return ResponseEntity.ok(mapToDto(user));
    }

    @Operation(summary = "Search users", description = "Search users by name, neptun code or email")
    @GetMapping("/search")
    public ResponseEntity<List<UserDto>> searchUsers(@RequestParam("query") String query) {
        log.info("Searching users with query: {}", query);
        List<User> users = userService.searchUsers(query);
        List<UserDto> userDtos = users.stream()
            .map(this::mapToDto)
            .collect(Collectors.toList());
        return ResponseEntity.ok(userDtos);
    }

    @Operation(summary = "Get all active users", description = "Get list of all active users")
    @GetMapping
    public ResponseEntity<List<UserDto>> getAllActiveUsers() {
        log.info("Request for all active users");
        List<User> users = userService.getAllActiveUsers();
        List<UserDto> userDtos = users.stream()
            .map(this::mapToDto)
            .collect(Collectors.toList());
        return ResponseEntity.ok(userDtos);
    }

    @Operation(summary = "Update last seen", description = "Update user's last seen timestamp")
    @PostMapping("/last-seen")
    public ResponseEntity<Void> updateLastSeen(@AuthenticationPrincipal User currentUser) {
        userService.updateLastSeen(currentUser.getId());
        return ResponseEntity.ok().build();
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
