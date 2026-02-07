package com.thesis.neptunmock.dto.exam;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * ExamRegistrationDto.java
 * Student exam registration details
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExamRegistrationDto {

    private Long examId;
    private String courseCode;
    private String courseName;
    private LocalDateTime examDate;
    private String location;
    private String registrationStatus;
    private LocalDateTime registrationDate;
    private String seatNumber;
}