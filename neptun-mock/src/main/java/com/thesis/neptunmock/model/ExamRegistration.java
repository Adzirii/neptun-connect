package com.thesis.neptunmock.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExamRegistration {

    private Long id;
    private Long studentId;
    private Long examId;
    private LocalDateTime registrationDate;
    private String status;
    private String seatNumber;
}
