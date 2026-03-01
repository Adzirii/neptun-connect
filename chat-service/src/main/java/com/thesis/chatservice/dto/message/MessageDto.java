package com.thesis.chatservice.dto.message;

import com.fasterxml.jackson.annotation.JsonView;
import com.thesis.chatservice.dto.user.UserDto;
import com.thesis.chatservice.view.Views;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageDto {

    @JsonView(Views.Public.class)
    private Long id;
    @JsonView(Views.Public.class)
    private Long conversationId;
    @JsonView(Views.Public.class)
    private UserDto sender;
    @JsonView(Views.Public.class)
    private String content;
    @JsonView(Views.Public.class)
    private String messageType;
    private Long parentMessageId;
    private ParentMessageDto parentMessage;
    private Boolean isEdited;
    private List<MessageAttachmentDto> attachments;
    private Long readCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}