package com.thesis.neptunmock.controller;

import com.thesis.neptunmock.dto.course.*;
import com.thesis.neptunmock.service.CourseService;
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
 * CourseController.java
 * REST Controller for course endpoints
 */
@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Courses", description = "Course management APIs")
@SecurityRequirement(name = "Bearer Authentication")
public class CourseController {

    private final CourseService courseService;

    @Operation(summary = "Get all courses", description = "Get list of all available courses")
    @GetMapping
    public ResponseEntity<List<CourseDto>> getAllCourses() {
        log.info("Request for all courses");
        List<CourseDto> courses = courseService.getAllCourses();
        return ResponseEntity.ok(courses);
    }

    @Operation(summary = "Get course by code", description = "Get detailed course information by course code")
    @GetMapping("/{courseCode}")
    public ResponseEntity<CourseDetailDto> getCourseByCode(@PathVariable("courseCode") String courseCode) {
        log.info("Request for course: {}", courseCode);
        CourseDetailDto course = courseService.getCourseByCode(courseCode);
        return ResponseEntity.ok(course);
    }

    @Operation(summary = "Get enrolled courses", description = "Get courses enrolled by the authenticated student")
    @GetMapping("/enrolled")
    public ResponseEntity<List<EnrolledCourseDto>> getEnrolledCourses(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        log.info("Request for enrolled courses: {}", userDetails.getUsername());
        List<EnrolledCourseDto> courses = courseService.getEnrolledCourses(userDetails.getUsername());
        return ResponseEntity.ok(courses);
    }

    @Operation(summary = "Get course students", description = "Get list of students enrolled in a course")
    @GetMapping("/{courseCode}/students")
    public ResponseEntity<List<CourseStudentDto>> getCourseStudents(@PathVariable("courseCode") String courseCode) {
        log.info("Request for students in course: {}", courseCode);
        List<CourseStudentDto> students = courseService.getCourseStudents(courseCode);
        return ResponseEntity.ok(students);
    }
}