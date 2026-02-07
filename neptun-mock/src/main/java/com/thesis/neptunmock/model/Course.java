package com.thesis.neptunmock.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Course {

    private Long id;
    private String courseCode;
    private String name;
    private Integer credits;
    private String instructor;
    private String instructorEmail;
    private String department;
    private String semester;
    private String type;
    private String description;
    private String syllabus;
    private List<String> prerequisites;
    private String assessmentMethod;
    private Integer maxStudents;
    private Integer enrolledStudents;
    private String language;
    private String location;
}