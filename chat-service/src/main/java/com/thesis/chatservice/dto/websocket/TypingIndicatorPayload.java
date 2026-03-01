package com.thesis.chatservice.dto.websocket;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TypingIndicatorPayload {
    private Long conversationId;
    private Long userId;
    private String userName;
    private Boolean isTyping;
}