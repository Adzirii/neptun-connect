package com.thesis.neptunmock.dto.exam;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * ExamDto.java
 * Exam information
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExamDto {

    private Long id;
    private String courseCode;
    private String courseName;
    private String examType;
    private LocalDateTime examDate;
    private String location;
    private String room;
    private Integer duration;
    private String instructor;
    private String status;
    private Integer maxStudents;
    private Integer registeredStudents;
}
