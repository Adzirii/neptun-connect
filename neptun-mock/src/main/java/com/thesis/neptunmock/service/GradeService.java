package com.thesis.neptunmock.service;

import com.thesis.neptunmock.dto.grade.GradeDto;
import com.thesis.neptunmock.dto.grade.SemesterGradesDto;
import com.thesis.neptunmock.model.Grade;
import com.thesis.neptunmock.model.Student;
import com.thesis.neptunmock.repository.MockDataRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class GradeService {

    private final MockDataRepository repository;

    public SemesterGradesDto getSemesterGrades(String neptunCode, String semester) {
        log.debug("Getting grades for student: {} semester: {}", neptunCode, semester);

        Student student = repository.findStudentByNeptunCode(neptunCode)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        List<Grade> grades = repository.findGradesByStudentIdAndSemester(student.getId(), semester);

        Integer totalCredits = grades.stream()
                .mapToInt(Grade::getCredits)
                .sum();

        Double semesterGpa = grades.stream()
                .mapToDouble(Grade::getGradeValue)
                .average()
                .orElse(0.0);

        return SemesterGradesDto.builder()
                .semester(semester)
                .totalCredits(totalCredits)
                .completedCredits(totalCredits)
                .semesterGpa(semesterGpa)
                .cumulativeGpa(student.getGpa())
                .grades(grades.stream().map(this::mapToDto).collect(Collectors.toList()))
                .build();
    }

    public List<GradeDto> getAllGrades(String neptunCode) {
        log.debug("Getting all grades for student: {}", neptunCode);

        Student student = repository.findStudentByNeptunCode(neptunCode)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        return repository.findGradesByStudentId(student.getId()).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    private GradeDto mapToDto(Grade grade) {
        return GradeDto.builder()
                .id(grade.getId())
                .courseCode(grade.getCourseCode())
                .courseName(grade.getCourseName())
                .credits(grade.getCredits())
                .grade(grade.getGrade())
                .gradeValue(grade.getGradeValue())
                .gradeDate(grade.getGradeDate())
                .semester(grade.getSemester())
                .instructor(grade.getInstructor())
                .examType(grade.getExamType())
                .build();
    }
}
