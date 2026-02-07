
package com.thesis.neptunmock.controller;

import com.thesis.neptunmock.dto.student.StudentDto;
import com.thesis.neptunmock.dto.student.StudentProfileDto;
import com.thesis.neptunmock.service.StudentService;
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
 * StudentController.java
 * REST Controller for student endpoints
 */
@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Students", description = "Student information management APIs")
@SecurityRequirement(name = "Bearer Authentication")
public class StudentController {

    private final StudentService studentService;

    @Operation(summary = "Get current student profile", description = "Get detailed profile of authenticated student")
    @GetMapping("/profile")
    public ResponseEntity<StudentProfileDto> getProfile(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        log.info("Profile request for student: {}", userDetails.getUsername());
        StudentProfileDto profile = studentService.getStudentProfile(userDetails.getUsername());
        return ResponseEntity.ok(profile);
    }

    @Operation(summary = "Get student by Neptun code", description = "Get student information by Neptun code")
    @GetMapping("/{neptunCode}")
    public ResponseEntity<StudentDto> getStudentByNeptunCode(
            @PathVariable("neptunCode") String neptunCode
    ) {
        log.info("Request for student: {}", neptunCode);
        StudentDto student = studentService.getStudentByNeptunCode(neptunCode);
        return ResponseEntity.ok(student);
    }

    @Operation(summary = "Search students", description = "Search students by name, neptun code or email")
    @GetMapping("/search")
    public ResponseEntity<List<StudentDto>> searchStudents(
            @RequestParam("query") String query
    ) {
        log.info("Searching students with query: {}", query);
        List<StudentDto> students = studentService.searchStudents(query);
        return ResponseEntity.ok(students);
    }
}