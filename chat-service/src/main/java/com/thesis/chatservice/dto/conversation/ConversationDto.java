package com.thesis.chatservice.dto.conversation;

import com.thesis.chatservice.dto.message.MessageDto;
import com.thesis.chatservice.dto.user.UserDto;
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
public class ConversationDto {

    private Long id;
    private String name;
    private String type;
    private String courseCode;
    private String description;
    private String avatarUrl;
    private UserDto createdBy;
    private List<UserDto> participants;
    private MessageDto lastMessage;
    private Long unreadCount;
    private LocalDateTime createdAt;
    private LocalDateTime lastMessageAt;
}
