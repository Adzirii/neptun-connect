package com.thesis.chatservice.dto.conversation;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateCourseConversationRequest {

    @NotBlank(message = "Course code is required")
    private String courseCode;

    private String name;
}

