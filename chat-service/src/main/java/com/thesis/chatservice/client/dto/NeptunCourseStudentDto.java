package com.thesis.chatservice.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NeptunCourseStudentDto {

    private Long id;
    private String neptunCode;
    private String name;
    private String email;
    private String program;
    private String enrollmentStatus;
}

