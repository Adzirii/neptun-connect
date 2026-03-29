package com.thesis.chatservice.controller;

import com.thesis.chatservice.dto.message.CreateMessageRequest;
import com.thesis.chatservice.dto.message.MessageDto;
import com.thesis.chatservice.dto.websocket.ChatMessagePayload;
import com.thesis.chatservice.dto.websocket.TypingIndicatorPayload;
import com.thesis.chatservice.entity.Message;
import com.thesis.chatservice.entity.User;
import com.thesis.chatservice.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ChatWebSocketController {

    private final MessageService messageService;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat/{conversationId}")
    public void handleChatMessage(
        @DestinationVariable Long conversationId,
        @Payload ChatMessagePayload payload,
        @AuthenticationPrincipal User user
    ) {
        log.info("Received chat message for conversation: {}", conversationId);

        try {
            CreateMessageRequest request = CreateMessageRequest.builder()
                .conversationId(conversationId)
                .content(payload.getContent())
                .messageType("TEXT")
                .build();

            Message message = messageService.createMessage(request, user);
            MessageDto messageDto = mapToDto(message);

            messagingTemplate.convertAndSend(
                "/topic/conversations/" + conversationId,
                messageDto
            );

            log.info("Message broadcasted to conversation: {}", conversationId);

        } catch (Exception e) {
            log.error("Error handling chat message: {}", e.getMessage(), e);
        }
    }

    @MessageMapping("/typing/{conversationId}")
    public void handleTypingIndicator(
        @DestinationVariable Long conversationId,
        @Payload TypingIndicatorPayload payload,
        @AuthenticationPrincipal User user
    ) {
        log.debug("Typing indicator for conversation: {}", conversationId);

        payload.setUserId(user.getId());
        payload.setUserName(user.getName());

        messagingTemplate.convertAndSend(
            "/topic/conversations/" + conversationId + "/typing",
            payload
        );
    }

    private MessageDto mapToDto(Message message) {
        return MessageDto.builder()
            .id(message.getId())
            .conversationId(message.getConversation().getId())
            .content(message.getContent())
            .messageType(message.getMessageType().name())
            .createdAt(message.getCreatedAt())
            .build();
    }
}
