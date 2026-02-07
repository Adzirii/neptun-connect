package com.thesis.neptunmock.dto.grade;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * GradeStatisticsDto.java
 * Statistical information about grades
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GradeStatisticsDto {

    private String courseCode;
    private Double average;
    private String median;
    private Integer totalStudents;
    private Integer passed;
    private Integer failed;
    private Map<String, Integer> gradeDistribution;
}