package com.thesis.chatservice.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NeptunCourseDto {

    private Long id;
    private String courseCode;
    private String name;
    private Integer credits;
    private String instructor;
    private String semester;
    private String status;
}