package com.thesis.neptunmock.service;

import com.thesis.neptunmock.dto.student.StudentDto;
import com.thesis.neptunmock.dto.student.StudentProfileDto;
import com.thesis.neptunmock.model.Student;
import com.thesis.neptunmock.repository.MockDataRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * StudentService.java
 * Business logic for student operations
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class StudentService {

    private final MockDataRepository repository;

    public StudentProfileDto getStudentProfile(String neptunCode) {
        log.debug("Getting profile for student: {}", neptunCode);

        Student student = repository.findStudentByNeptunCode(neptunCode)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        return mapToProfileDto(student);
    }

    public StudentDto getStudentByNeptunCode(String neptunCode) {
        log.debug("Getting student by neptun code: {}", neptunCode);

        Student student = repository.findStudentByNeptunCode(neptunCode)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        return mapToDto(student);
    }

    public List<StudentDto> searchStudents(String query) {
        log.debug("Searching students with query: {}", query);

        return repository.findAllStudents().stream()
                .filter(student ->
                        student.getName().toLowerCase().contains(query.toLowerCase()) ||
                                student.getNeptunCode().toLowerCase().contains(query.toLowerCase()) ||
                                student.getEmail().toLowerCase().contains(query.toLowerCase())
                )
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    private StudentDto mapToDto(Student student) {
        return StudentDto.builder()
                .id(student.getId())
                .neptunCode(student.getNeptunCode())
                .name(student.getName())
                .email(student.getEmail())
                .program(student.getProgram())
                .faculty(student.getFaculty())
                .semester(student.getSemester())
                .enrollmentDate(student.getEnrollmentDate())
                .status(student.getStatus())
                .build();
    }

    private StudentProfileDto mapToProfileDto(Student student) {
        return StudentProfileDto.builder()
                .id(student.getId())
                .neptunCode(student.getNeptunCode())
                .name(student.getName())
                .email(student.getEmail())
                .program(student.getProgram())
                .faculty(student.getFaculty())
                .semester(student.getSemester())
                .enrollmentDate(student.getEnrollmentDate())
                .birthDate(student.getBirthDate())
                .phoneNumber(student.getPhoneNumber())
                .address(student.getAddress())
                .status(student.getStatus())
                .gpa(student.getGpa())
                .completedCredits(student.getCompletedCredits())
                .requiredCredits(student.getRequiredCredits())
                .build();
    }
}
