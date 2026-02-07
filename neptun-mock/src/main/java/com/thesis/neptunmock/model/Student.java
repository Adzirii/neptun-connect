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
public class Student {

    private Long id;
    private String neptunCode;
    private String password;
    private String name;
    private String email;
    private String program;
    private String faculty;
    private Integer semester;
    private LocalDate enrollmentDate;
    private LocalDate birthDate;
    private String phoneNumber;
    private String address;
    private String status;
    private Double gpa;
    private Integer completedCredits;
    private Integer requiredCredits;
}
