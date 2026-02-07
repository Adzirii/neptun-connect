package com.thesis.neptunmock.dto.course;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * CourseDto.java
 * Basic course information
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseDto {

    private Long id;
    private String courseCode;
    private String name;
    private Integer credits;
    private String instructor;
    private String department;
    private String semester;
    private String type;
    private Integer maxStudents;
    private Integer enrolledStudents;
}