package com.thesis.neptunmock.dto.grade;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * GradeDto.java
 * Grade information for a course
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GradeDto {

    private Long id;
    private String courseCode;
    private String courseName;
    private Integer credits;
    private String grade;
    private Integer gradeValue;
    private LocalDate gradeDate;
    private String semester;
    private String instructor;
    private String examType;
}
