package com.thesis.chatservice.controller;

import com.thesis.chatservice.dto.conversation.ConversationDto;
import com.thesis.chatservice.dto.conversation.CreateConversationRequest;
import com.thesis.chatservice.dto.conversation.CreateCourseConversationRequest;
import com.thesis.chatservice.dto.conversation.UpdateConversationRequest;
import com.thesis.chatservice.dto.user.UserDto;
import com.thesis.chatservice.entity.Conversation;
import com.thesis.chatservice.entity.ConversationParticipant;
import com.thesis.chatservice.entity.User;
import com.thesis.chatservice.service.ConversationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/conversations")
@RequiredArgsConstructor
@Slf4j
@Tag(
    name = "Conversations",
    description = "Conversation management APIs"
)
@SecurityRequirement(name = "Bearer Authentication")
public class ConversationController {

    private final ConversationService conversationService;

    @Operation(
        summary = "Get user conversations",
        description = "Get all conversations for the authenticated user"
    )
    @GetMapping
    public ResponseEntity<List<ConversationDto>> getUserConversations(
        @AuthenticationPrincipal User currentUser
    ) {
        log.info("Request for conversations of user: {}", currentUser.getId());
        List<Conversation> conversations = conversationService.getUserConversations(currentUser.getId());
        List<ConversationDto> conversationDtos = conversations.stream()
            .map(this::mapToDto)
            .collect(Collectors.toList());
        return ResponseEntity.ok(conversationDtos);
    }

    @Operation(
        summary = "Get conversation by ID",
        description = "Get conversation details by ID"
    )
    @GetMapping("/{conversationId}")
    public ResponseEntity<ConversationDto> getConversationById(
        @PathVariable("conversationId") Long conversationId,
        @AuthenticationPrincipal User currentUser
    ) {
        log.info("Request for conversation: {} by user: {}", conversationId, currentUser.getId());
        conversationService.validateUserAccess(conversationId, currentUser.getId());
        Conversation conversation = conversationService.getConversationById(conversationId);
        return ResponseEntity.ok(mapToDto(conversation));
    }

    @Operation(
        summary = "Create conversation",
        description = "Create a new conversation"
    )
    @PostMapping
    public ResponseEntity<ConversationDto> createConversation(
        @Valid @RequestBody CreateConversationRequest request,
        @AuthenticationPrincipal User currentUser
    ) {
        log.info("Creating conversation by user: {}", currentUser.getId());
        Conversation conversation = conversationService.createConversation(request, currentUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapToDto(conversation));
    }

    @Operation(
        summary = "Create course conversation",
        description = "Create a conversation for a course with all enrolled students"
    )
    @PostMapping("/course")
    public ResponseEntity<ConversationDto> createCourseConversation(
        @Valid @RequestBody CreateCourseConversationRequest request,
        @RequestHeader("X-Neptun-Token") String neptunToken,
        @AuthenticationPrincipal User currentUser
    ) {
        log.info("Creating course conversation for {} by user: {}", request.getCourseCode(), currentUser.getId());
        Conversation conversation = conversationService.createCourseConversation(
            request.getCourseCode(),
            request.getName(),
            currentUser,
            neptunToken
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(mapToDto(conversation));
    }

    @Operation(
        summary = "Add participant",
        description = "Add a user to a conversation"
    )
    @PostMapping("/{conversationId}/participants/{userId}")
    public ResponseEntity<Void> addParticipant(
        @PathVariable("conversationId") Long conversationId,
        @PathVariable("userId") Long userId,
        @AuthenticationPrincipal User currentUser
    ) {
        log.info("Adding user {} to conversation {} by user {}", userId, conversationId, currentUser.getId());
        conversationService.validateUserAccess(conversationId, currentUser.getId());
        conversationService.addParticipant(conversationId, userId, ConversationParticipant.ParticipantRole.MEMBER);
        return ResponseEntity.ok().build();
    }

    @Operation(
        summary = "Remove participant",
        description = "Remove a user from a conversation"
    )
    @DeleteMapping("/{conversationId}/participants/{userId}")
    public ResponseEntity<Void> removeParticipant(
        @PathVariable("conversationId") Long conversationId,
        @PathVariable("userId") Long userId,
        @AuthenticationPrincipal User currentUser
    ) {
        log.info("Removing user {} from conversation {} by user {}", userId, conversationId, currentUser.getId());
        conversationService.validateUserAccess(conversationId, currentUser.getId());
        conversationService.removeParticipant(conversationId, userId);
        return ResponseEntity.ok().build();
    }

    @Operation(
        summary = "Get conversation participants",
        description = "Get all participants of a conversation"
    )
    @GetMapping("/{conversationId}/participants")
    public ResponseEntity<List<UserDto>> getParticipants(
        @PathVariable("conversationId") Long conversationId,
        @AuthenticationPrincipal User currentUser
    ) {
        log.info("Request for participants of conversation: {}", conversationId);
        conversationService.validateUserAccess(conversationId, currentUser.getId());
        List<ConversationParticipant> participants = conversationService.getConversationParticipants(conversationId);
        List<UserDto> userDtos = participants.stream()
            .map(p -> mapUserToDto(p.getUser()))
            .collect(Collectors.toList());
        return ResponseEntity.ok(userDtos);
    }

    @Operation(
        summary = "Update conversation",
        description = "Update conversation name and description"
    )
    @PutMapping("/{conversationId}")
    public ResponseEntity<ConversationDto> updateConversation(
        @PathVariable("conversationId") Long conversationId,
        @Valid @RequestBody UpdateConversationRequest request,
        @AuthenticationPrincipal User currentUser
    ) {
        log.info("Updating conversation: {} by user: {}", conversationId, currentUser.getId());
        Conversation conversation = conversationService.updateConversation(conversationId, request, currentUser);
        return ResponseEntity.ok(mapToDto(conversation));
    }

    @Operation(
        summary = "Delete conversation",
        description = "Delete a conversation (soft delete - removes all participants)"
    )
    @DeleteMapping("/{conversationId}")
    public ResponseEntity<Void> deleteConversation(
        @PathVariable("conversationId") Long conversationId,
        @AuthenticationPrincipal User currentUser
    ) {
        log.info("Deleting conversation: {} by user: {}", conversationId, currentUser.getId());
        conversationService.deleteConversation(conversationId, currentUser);
        return ResponseEntity.ok().build();
    }

    private ConversationDto mapToDto(Conversation conversation) {
        List<ConversationParticipant> participants = conversationService.getConversationParticipants(conversation.getId());
        List<UserDto> participantDtos = participants.stream()
            .map(p -> mapUserToDto(p.getUser()))
            .collect(Collectors.toList());

        return ConversationDto.builder()
            .id(conversation.getId())
            .name(conversation.getName())
            .type(conversation.getType().name())
            .courseCode(conversation.getCourseCode())
            .description(conversation.getDescription())
            .avatarUrl(conversation.getAvatarUrl())
            .createdBy(conversation.getCreatedBy() != null ? mapUserToDto(conversation.getCreatedBy()) : null)
            .participants(participantDtos)
            .createdAt(conversation.getCreatedAt())
            .lastMessageAt(conversation.getLastMessageAt())
            .build();
    }

    private UserDto mapUserToDto(User user) {
        return UserDto.builder()
            .id(user.getId())
            .neptunCode(user.getNeptunCode())
            .name(user.getName())
            .email(user.getEmail())
            .avatarUrl(user.getAvatarUrl())
            .build();
    }

}
