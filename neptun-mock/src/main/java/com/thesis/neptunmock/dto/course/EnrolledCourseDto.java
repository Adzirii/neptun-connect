package com.thesis.neptunmock.dto.course;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * EnrolledCourseDto.java
 * Course information with enrollment details
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EnrolledCourseDto {

    private Long id;
    private String courseCode;
    private String name;
    private Integer credits;
    private String instructor;
    private String semester;
    private String status;
    private String grade;
    private Integer attendance;
}
