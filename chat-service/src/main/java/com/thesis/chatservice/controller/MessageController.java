package com.thesis.chatservice.controller;

import com.thesis.chatservice.dto.message.CreateMessageRequest;
import com.thesis.chatservice.dto.message.MessageAttachmentDto;
import com.thesis.chatservice.dto.message.MessageDto;
import com.thesis.chatservice.dto.message.ParentMessageDto;
import com.thesis.chatservice.dto.user.UserDto;
import com.thesis.chatservice.entity.Message;
import com.thesis.chatservice.entity.MessageAttachment;
import com.thesis.chatservice.entity.User;
import com.thesis.chatservice.service.ConversationService;
import com.thesis.chatservice.service.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
@Slf4j
@Tag(
    name = "Messages",
    description = "Message management APIs"
)
@SecurityRequirement(name = "Bearer Authentication")
public class MessageController {

    private final MessageService messageService;
    private final ConversationService conversationService;

    @Operation(
        summary = "Get conversation messages",
        description = "Get paginated messages for a conversation"
    )
    @GetMapping("/conversation/{conversationId}")
    public ResponseEntity<Page<MessageDto>> getConversationMessages(
        @PathVariable("conversationId") Long conversationId,
        @RequestParam(name = "page", defaultValue = "0") int page,
        @RequestParam(name = "size", defaultValue = "50") int size,
        @AuthenticationPrincipal User currentUser
    ) {
        log.info("Request for messages in conversation: {} by user: {}", conversationId, currentUser.getId());
        conversationService.validateUserAccess(conversationId, currentUser.getId());

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Message> messages = messageService.getConversationMessages(conversationId, pageable);
        Page<MessageDto> messageDtos =  messages.map(this::mapToDto);

        return ResponseEntity.ok(messageDtos);
    }

    @Operation(
        summary = "Create message",
        description = "Send a new message in a conversation"
    )
    @PostMapping
    public ResponseEntity<MessageDto> createMessage(
        @Valid @RequestBody CreateMessageRequest request,
        @AuthenticationPrincipal User currentUser
    ) {
        log.info(
            "Creating message in conversation: {} by user: {}",
            request.getConversationId(), currentUser.getId()
        );
        Message message = messageService.createMessage(request, currentUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapToDto(message));
    }

    @Operation(
        summary = "Update message",
        description = "Update an existing message"
    )
    @PutMapping("/{messageId}")
    public ResponseEntity<MessageDto> updateMessage(
        @PathVariable("messageId") Long messageId,
        @RequestBody Map<String, String> request,
        @AuthenticationPrincipal User currentUser
    ) {
        log.info("Updating message: {} by user: {}", messageId, currentUser.getId());
        String newContent = request.get("content");
        Message message = messageService.updateMessage(messageId, newContent, currentUser);
        return ResponseEntity.ok(mapToDto(message));
    }

    @Operation(
        summary = "Delete message",
        description = "Delete a message"
    )
    @DeleteMapping("/{messageId}")
    public ResponseEntity<Void> deleteMessage(
        @PathVariable("messageId") Long messageId,
        @AuthenticationPrincipal User currentUser
    ) {
        log.info("Deleting message: {} by user: {}", messageId, currentUser.getId());
        messageService.deleteMessage(messageId, currentUser);
        return ResponseEntity.ok().build();
    }

    @Operation(
        summary = "Mark message as read",
        description = "Mark a message as read"
    )
    @PostMapping("/{messageId}/read")
    public ResponseEntity<Void> markMessageAsRead(
        @PathVariable("messageId") Long messageId,
        @AuthenticationPrincipal User currentUser
    ) {
        messageService.markMessageAsRead(messageId, currentUser);
        return ResponseEntity.ok().build();
    }

    @Operation(
        summary = "Mark conversation as read",
        description = "Mark all messages in a conversation as read"
    )
    @PostMapping("/conversation/{conversationId}/read")
    public ResponseEntity<Void> markConversationAsRead(
        @PathVariable("conversationId") Long conversationId,
        @RequestParam(required = false) String since,
        @AuthenticationPrincipal User currentUser
    ) {
        LocalDateTime sinceTime = since != null
            ? LocalDateTime.parse(since)
            : LocalDateTime.now().minusDays(30);
        messageService.markConversationMessagesAsRead(conversationId, currentUser, sinceTime);
        return ResponseEntity.ok().build();
    }

    @Operation(
        summary = "Search messages",
        description = "Search messages in a conversation"
    )
    @GetMapping("/conversation/{conversationId}/search")
    public ResponseEntity<Page<MessageDto>> searchMessages(
        @PathVariable("conversationId") Long conversationId,
        @RequestParam("query") String query,
        @RequestParam(
            name = "page",
            defaultValue = "0"
        ) int page,
        @RequestParam(
            name = "size",
            defaultValue = "20"
        ) int size,
        @AuthenticationPrincipal User currentUser
    ) {
        log.info("Searching messages in conversation: {} with query: {}", conversationId, query);
        conversationService.validateUserAccess(conversationId, currentUser.getId());

        Pageable pageable = PageRequest.of(page, size);
        Page<Message> messages = messageService.searchMessages(conversationId, query, pageable);
        Page<MessageDto> messageDtos = messages.map(this::mapToDto);

        return ResponseEntity.ok(messageDtos);
    }

    private MessageDto mapToDto(Message message) {
        ParentMessageDto parentMessageDto = null;
        if (message.getParentMessage() != null) {
            Message parent = message.getParentMessage();
            parentMessageDto = ParentMessageDto.builder()
                .id(parent.getId())
                .sender(mapUserToDto(parent.getSender()))
                .content(parent.getContent())
                .build();
        }

        List<MessageAttachmentDto> attachmentDtos = null;
        if (message.getAttachments() != null && !message.getAttachments().isEmpty()) {
            attachmentDtos = message.getAttachments().stream()
                .map(this::mapAttachmentToDto)
                .collect(Collectors.toList());
        }

        return MessageDto.builder()
            .id(message.getId())
            .conversationId(message.getConversation().getId())
            .sender(mapUserToDto(message.getSender()))
            .content(message.getContent())
            .messageType(message.getMessageType().name())
            .parentMessageId(message.getParentMessage() != null
                ? message.getParentMessage().getId()
                : null)
            .parentMessage(parentMessageDto)
            .isEdited(message.getIsEdited())
            .attachments(attachmentDtos)
            .readCount(messageService.getReadCount(message.getId()))
            .createdAt(message.getCreatedAt())
            .updatedAt(message.getUpdatedAt())
            .build();
    }

    private MessageAttachmentDto mapAttachmentToDto(MessageAttachment attachment) {
        return MessageAttachmentDto.builder()
            .id(attachment.getId())
            .fileName(attachment.getFileName())
            .fileType(attachment.getFileType())
            .fileSize(attachment.getFileSize())
            .fileUrl(attachment.getFileUrl())
            .thumbnailUrl(attachment.getThumbnailUrl())
            .build();
    }

    private UserDto mapUserToDto(User user) {
        return UserDto.builder()
            .id(user.getId())
            .neptunCode(user.getNeptunCode())
            .name(user.getName())
            .avatarUrl(user.getAvatarUrl())
            .build();
    }

}