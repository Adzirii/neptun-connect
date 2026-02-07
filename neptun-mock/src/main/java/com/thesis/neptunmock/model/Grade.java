package com.thesis.neptunmock.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Grade {

    private Long id;
    private Long studentId;
    private Long courseId;
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