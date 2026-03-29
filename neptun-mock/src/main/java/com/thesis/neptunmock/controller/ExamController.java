package com.thesis.neptunmock.controller;

import com.thesis.neptunmock.dto.exam.ExamDto;
import com.thesis.neptunmock.service.ExamService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * ExamController.java
 * REST Controller for exam endpoints
 */
@RestController
@RequestMapping("/api/exams")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Exams", description = "Exam management APIs")
@SecurityRequirement(name = "Bearer Authentication")
public class ExamController {

    private final ExamService examService;

    @Operation(summary = "Get upcoming exams", description = "Get list of all upcoming exams")
    @GetMapping("/upcoming")
    public ResponseEntity<List<ExamDto>> getUpcomingExams() {
        log.info("Request for upcoming exams");
        List<ExamDto> exams = examService.getUpcomingExams();
        return ResponseEntity.ok(exams);
    }

    @Operation(summary = "Get exam by ID", description = "Get detailed exam information by exam ID")
    @GetMapping("/{examId}")
    public ResponseEntity<ExamDto> getExamById(@PathVariable("examId") Long examId) {
        log.info("Request for exam: {}", examId);
        ExamDto exam = examService.getExamById(examId);
        return ResponseEntity.ok(exam);
    }
}
