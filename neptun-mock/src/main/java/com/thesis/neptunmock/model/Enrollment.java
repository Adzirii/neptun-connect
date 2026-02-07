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
public class Enrollment {

    private Long id;
    private Long studentId;
    private Long courseId;
    private String semester;
    private String status;
    private LocalDate enrollmentDate;
    private String grade;
    private Integer attendance;
}

