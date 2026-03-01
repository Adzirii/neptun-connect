package com.thesis.chatservice.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NeptunStudentDto {

    private Long id;
    private String neptunCode;
    private String name;
    private String email;
    private String program;
    private String faculty;
    private Integer semester;
    private LocalDate enrollmentDate;
    private String status;
}