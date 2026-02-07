package com.thesis.neptunmock.service;

import com.thesis.neptunmock.dto.exam.ExamDto;
import com.thesis.neptunmock.model.Exam;
import com.thesis.neptunmock.repository.MockDataRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExamService {

    private final MockDataRepository repository;

    public List<ExamDto> getUpcomingExams() {
        log.debug("Getting upcoming exams");
        return repository.findUpcomingExams().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public ExamDto getExamById(Long examId) {
        log.debug("Getting exam by id: {}", examId);
        Exam exam = repository.findExamById(examId)
                .orElseThrow(() -> new RuntimeException("Exam not found"));
        return mapToDto(exam);
    }

    private ExamDto mapToDto(Exam exam) {
        return ExamDto.builder()
                .id(exam.getId())
                .courseCode(exam.getCourseCode())
                .courseName(exam.getCourseName())
                .examType(exam.getExamType())
                .examDate(exam.getExamDate())
                .location(exam.getLocation())
                .room(exam.getRoom())
                .duration(exam.getDuration())
                .instructor(exam.getInstructor())
                .status(exam.getStatus())
                .maxStudents(exam.getMaxStudents())
                .registeredStudents(exam.getRegisteredStudents())
                .build();
    }
}