package com.thesis.neptunmock.service;

import com.thesis.neptunmock.dto.course.*;
import com.thesis.neptunmock.dto.student.StudentDto;
import com.thesis.neptunmock.model.Course;
import com.thesis.neptunmock.model.Enrollment;
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
public class CourseService {

    private final MockDataRepository repository;

    public List<CourseDto> getAllCourses() {
        log.debug("Getting all courses");
        return repository.findAllCourses().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public CourseDetailDto getCourseByCode(String courseCode) {
        log.debug("Getting course by code: {}", courseCode);
        Course course = repository.findCourseByCode(courseCode)
                .orElseThrow(() -> new RuntimeException("Course not found: " + courseCode));
        return mapToDetailDto(course);
    }

    public List<EnrolledCourseDto> getEnrolledCourses(String neptunCode) {
        log.debug("Getting enrolled courses for student: {}", neptunCode);

        Student student = repository.findStudentByNeptunCode(neptunCode)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        List<Enrollment> enrollments = repository.findEnrollmentsByStudentId(student.getId());

        return enrollments.stream()
                .map(enrollment -> {
                    Course course = repository.findCourseById(enrollment.getCourseId())
                            .orElseThrow(() -> new RuntimeException("Course not found"));
                    return mapToEnrolledCourseDto(course, enrollment);
                })
                .collect(Collectors.toList());
    }

    public List<CourseStudentDto> getCourseStudents(String courseCode) {
        log.debug("Getting students for course: {}", courseCode);

        Course course = repository.findCourseByCode(courseCode)
                .orElseThrow(() -> new RuntimeException("Course not found: " + courseCode));

        List<Enrollment> enrollments = repository.findEnrollmentsByCourseId(course.getId());

        return enrollments.stream()
                .map(enrollment -> {
                    Student student = repository.findStudentById(enrollment.getStudentId())
                            .orElseThrow(() -> new RuntimeException("Student not found"));
                    return mapToCourseStudentDto(student, enrollment);
                })
                .collect(Collectors.toList());
    }

    private CourseDto mapToDto(Course course) {
        return CourseDto.builder()
                .id(course.getId())
                .courseCode(course.getCourseCode())
                .name(course.getName())
                .credits(course.getCredits())
                .instructor(course.getInstructor())
                .department(course.getDepartment())
                .semester(course.getSemester())
                .type(course.getType())
                .maxStudents(course.getMaxStudents())
                .enrolledStudents(course.getEnrolledStudents())
                .build();
    }

    private CourseDetailDto mapToDetailDto(Course course) {
        return CourseDetailDto.builder()
                .id(course.getId())
                .courseCode(course.getCourseCode())
                .name(course.getName())
                .credits(course.getCredits())
                .instructor(course.getInstructor())
                .instructorEmail(course.getInstructorEmail())
                .department(course.getDepartment())
                .semester(course.getSemester())
                .type(course.getType())
                .description(course.getDescription())
                .syllabus(course.getSyllabus())
                .prerequisites(course.getPrerequisites())
                .assessmentMethod(course.getAssessmentMethod())
                .maxStudents(course.getMaxStudents())
                .enrolledStudents(course.getEnrolledStudents())
                .language(course.getLanguage())
                .location(course.getLocation())
                .build();
    }

    private EnrolledCourseDto mapToEnrolledCourseDto(Course course, Enrollment enrollment) {
        return EnrolledCourseDto.builder()
                .id(course.getId())
                .courseCode(course.getCourseCode())
                .name(course.getName())
                .credits(course.getCredits())
                .instructor(course.getInstructor())
                .semester(enrollment.getSemester())
                .status(enrollment.getStatus())
                .grade(enrollment.getGrade())
                .attendance(enrollment.getAttendance())
                .build();
    }

    private CourseStudentDto mapToCourseStudentDto(Student student, Enrollment enrollment) {
        return CourseStudentDto.builder()
                .id(student.getId())
                .neptunCode(student.getNeptunCode())
                .name(student.getName())
                .email(student.getEmail())
                .program(student.getProgram())
                .enrollmentStatus(enrollment.getStatus())
                .build();
    }
}