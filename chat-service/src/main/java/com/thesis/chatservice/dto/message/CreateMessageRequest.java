package com.thesis.chatservice.dto.message;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateMessageRequest {

    @NotNull(message = "Conversation ID is required")
    private Long conversationId;

    @NotBlank(message = "Content is required")
    private String content;

    private String messageType;

    private Long parentMessageId;

    private List<AttachmentInfo> attachments;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AttachmentInfo {
        private String fileName;
        private String fileType;
        private Long fileSize;
        private String fileUrl;
    }
}