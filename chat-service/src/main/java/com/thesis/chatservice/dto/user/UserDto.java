package com.thesis.chatservice.dto.user;

import com.fasterxml.jackson.annotation.JsonView;
import com.thesis.chatservice.view.Views;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {
    @JsonView(Views.Public.class)
    private Long id;
    @JsonView(Views.Public.class)
    private String neptunCode;
    @JsonView(Views.Public.class)
    private String name;
    private String email;
    private String program;
    private String faculty;
    private Integer semester;
    private String status;
    @JsonView(Views.Public.class)
    private String avatarUrl;
    private LocalDateTime lastSeenAt;
    private Boolean online;
}
