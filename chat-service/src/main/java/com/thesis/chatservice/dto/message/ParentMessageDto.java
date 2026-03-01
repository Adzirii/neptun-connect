package com.thesis.chatservice.dto.message;

import com.fasterxml.jackson.annotation.JsonView;
import com.thesis.chatservice.dto.user.UserDto;
import com.thesis.chatservice.view.Views;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParentMessageDto {

    @JsonView(Views.Public.class)
    private Long id;

    @JsonView(Views.Public.class)
    private UserDto sender;

    @JsonView(Views.Public.class)
    private String content;
}

