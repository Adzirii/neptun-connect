package com.thesis.neptunmock.dto.course;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * CourseStudentDto.java
 * Student information in course context
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseStudentDto {

    private Long id;
    private String neptunCode;
    private String name;
    private String email;
    private String program;
    private String enrollmentStatus;
}
