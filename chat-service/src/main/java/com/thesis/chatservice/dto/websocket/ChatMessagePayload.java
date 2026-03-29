package com.thesis.chatservice.dto.websocket;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessagePayload {
    private String type;
    private Long conversationId;
    private String content;
    private Long messageId;
}
