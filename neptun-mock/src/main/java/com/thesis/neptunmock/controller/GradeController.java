package com.thesis.neptunmock.controller;

import com.thesis.neptunmock.dto.grade.GradeDto;
import com.thesis.neptunmock.dto.grade.SemesterGradesDto;
import com.thesis.neptunmock.service.GradeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * GradeController.java
 * REST Controller for grade endpoints
 */
@RestController
@RequestMapping("/api/grades")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Grades", description = "Grade management APIs")
@SecurityRequirement(name = "Bearer Authentication")
public class GradeController {

    private final GradeService gradeService;

    @Operation(summary = "Get semester grades", description = "Get all grades for a specific semester")
    @GetMapping("/semester/{semester}")
    public ResponseEntity<SemesterGradesDto> getSemesterGrades(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable("semester") String semester
    ) {
        log.info("Request for semester grades: {} - {}", userDetails.getUsername(), semester);
        SemesterGradesDto grades = gradeService.getSemesterGrades(userDetails.getUsername(), semester);
        return ResponseEntity.ok(grades);
    }

    @Operation(summary = "Get all grades", description = "Get all grades for authenticated student")
    @GetMapping("/all")
    public ResponseEntity<List<GradeDto>> getAllGrades(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        log.info("Request for all grades: {}", userDetails.getUsername());
        List<GradeDto> grades = gradeService.getAllGrades(userDetails.getUsername());
        return ResponseEntity.ok(grades);
    }
}