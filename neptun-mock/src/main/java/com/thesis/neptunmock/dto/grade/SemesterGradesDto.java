package com.thesis.neptunmock.dto.grade;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * SemesterGradesDto.java
 * All grades for a semester
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SemesterGradesDto {

    private String semester;
    private Integer totalCredits;
    private Integer completedCredits;
    private Double semesterGpa;
    private Double cumulativeGpa;
    private List<GradeDto> grades;
}