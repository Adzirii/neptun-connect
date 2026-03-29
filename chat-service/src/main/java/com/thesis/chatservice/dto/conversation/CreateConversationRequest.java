package com.thesis.chatservice.dto.conversation;

import jakarta.validation.constraints.NotEmpty;
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
public class CreateConversationRequest {

    private String name;

    @NotNull(message = "Conversation type is required")
    private String type;

    private String courseCode;

    private String description;

    @NotEmpty(message = "At least one participant is required")
    private List<Long> participantIds;
}
