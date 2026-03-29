package com.thesis.neptunmock.repository;

import com.thesis.neptunmock.model.Course;
import com.thesis.neptunmock.model.Enrollment;
import com.thesis.neptunmock.model.Exam;
import com.thesis.neptunmock.model.ExamRegistration;
import com.thesis.neptunmock.model.Grade;
import com.thesis.neptunmock.model.Student;
import com.thesis.neptunmock.model.TimetableEntry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;


/**
 * MockDataRepository.java
 * In-memory data storage for mock Neptun API
 */
@Repository
@Slf4j
public class MockDataRepository {

    private final Map<Long, Student> students = new HashMap<>();
    private final Map<String, Student> studentsByNeptunCode = new HashMap<>();
    private final Map<Long, Course> courses = new HashMap<>();
    private final Map<String, Course> coursesByCode = new HashMap<>();
    private final Map<Long, Enrollment> enrollments = new HashMap<>();
    private final Map<Long, TimetableEntry> timetableEntries = new HashMap<>();
    private final Map<Long, Exam> exams = new HashMap<>();
    private final Map<Long, ExamRegistration> examRegistrations = new HashMap<>();
    private final Map<Long, Grade> grades = new HashMap<>();

    private Long studentIdCounter = 1L;
    private Long courseIdCounter = 1L;
    private Long enrollmentIdCounter = 1L;
    private Long timetableIdCounter = 1L;
    private Long examIdCounter = 1L;
    private Long examRegistrationIdCounter = 1L;
    private Long gradeIdCounter = 1L;

    public MockDataRepository(PasswordEncoder passwordEncoder) {
        initializeMockData(passwordEncoder);
    }

    private void initializeMockData(PasswordEncoder passwordEncoder) {
        log.info("Initializing mock data...");

        createStudents(passwordEncoder);
        createCourses();
        createEnrollments();
        createTimetableEntries();
        createExams();
        createGrades();

        log.info("Mock data initialized successfully");
    }

    private void createStudents(PasswordEncoder passwordEncoder) {
        List<Student> studentList = Arrays.asList(
            Student.builder()
                .id(studentIdCounter++)
                .neptunCode("ABC123")
                .password(passwordEncoder.encode("password"))
                .name("John Doe")
                .email("john.doe@student.uni.hu")
                .program("Computer Science BSc")
                .faculty("Faculty of Engineering")
                .semester(5)
                .enrollmentDate(LocalDate.of(2022, 9, 1))
                .birthDate(LocalDate.of(2002, 5, 15))
                .phoneNumber("+36301234567")
                .address("Budapest, Hungary")
                .status("ACTIVE")
                .gpa(4.2)
                .completedCredits(120)
                .requiredCredits(180)
                .build(),

            Student.builder()
                .id(studentIdCounter++)
                .neptunCode("DEF456")
                .password(passwordEncoder.encode("password"))
                .name("Jane Smith")
                .email("jane.smith@student.uni.hu")
                .program("Software Engineering BSc")
                .faculty("Faculty of Engineering")
                .semester(3)
                .enrollmentDate(LocalDate.of(2023, 9, 1))
                .birthDate(LocalDate.of(2003, 8, 22))
                .phoneNumber("+36302345678")
                .address("Budapest, Hungary")
                .status("ACTIVE")
                .gpa(4.5)
                .completedCredits(60)
                .requiredCredits(180)
                .build(),

            Student.builder()
                .id(studentIdCounter++)
                .neptunCode("GHI789")
                .password(passwordEncoder.encode("password"))
                .name("Peter Nagy")
                .email("peter.nagy@student.uni.hu")
                .program("Computer Science BSc")
                .faculty("Faculty of Engineering")
                .semester(7)
                .enrollmentDate(LocalDate.of(2021, 9, 1))
                .birthDate(LocalDate.of(2001, 3, 10))
                .phoneNumber("+36303456789")
                .address("Budapest, Hungary")
                .status("ACTIVE")
                .gpa(3.8)
                .completedCredits(160)
                .requiredCredits(180)
                .build(),

            Student.builder()
                .id(studentIdCounter++)
                .neptunCode("BLOMOE")
                .password(passwordEncoder.encode("password"))
                .name("Nikita Liubov")
                .email("BLOMOE@pte.hu")
                .program("Computer Science BSc")
                .faculty("Faculty of Engineering")
                .semester(5)
                .enrollmentDate(LocalDate.of(2023, 9, 1))
                .birthDate(LocalDate.of(2002, 6, 30))
                .phoneNumber("+36303456789")
                .address("Pecs, Hungary")
                .status("ACTIVE")
                .gpa(3.8)
                .completedCredits(160)
                .requiredCredits(180)
                .build(),

            Student.builder()
                .id(studentIdCounter++)
                .neptunCode("JKL012")
                .password(passwordEncoder.encode("password"))
                .name("Anna Horvath")
                .email("anna.horvath@student.uni.hu")
                .program("Information Technology BSc")
                .faculty("Faculty of Engineering")
                .semester(4)
                .enrollmentDate(LocalDate.of(2023, 2, 1))
                .birthDate(LocalDate.of(2002, 11, 5))
                .phoneNumber("+36304567890")
                .address("Budapest, Hungary")
                .status("ACTIVE")
                .gpa(4.1)
                .completedCredits(90)
                .requiredCredits(180)
                .build(),

            Student.builder()
                .id(studentIdCounter++)
                .neptunCode("MNO345")
                .password(passwordEncoder.encode("password"))
                .name("Balazs Szabo")
                .email("balazs.szabo@student.uni.hu")
                .program("Computer Science BSc")
                .faculty("Faculty of Engineering")
                .semester(6)
                .enrollmentDate(LocalDate.of(2022, 2, 1))
                .birthDate(LocalDate.of(2001, 7, 18))
                .phoneNumber("+36305678901")
                .address("Debrecen, Hungary")
                .status("ACTIVE")
                .gpa(3.9)
                .completedCredits(140)
                .requiredCredits(180)
                .build(),

            Student.builder()
                .id(studentIdCounter++)
                .neptunCode("PQR678")
                .password(passwordEncoder.encode("password"))
                .name("Eva Kiss")
                .email("eva.kiss@student.uni.hu")
                .program("Software Engineering BSc")
                .faculty("Faculty of Engineering")
                .semester(2)
                .enrollmentDate(LocalDate.of(2024, 2, 1))
                .birthDate(LocalDate.of(2004, 1, 25))
                .phoneNumber("+36306789012")
                .address("Szeged, Hungary")
                .status("ACTIVE")
                .gpa(4.6)
                .completedCredits(30)
                .requiredCredits(180)
                .build(),

            Student.builder()
                .id(studentIdCounter++)
                .neptunCode("STU901")
                .password(passwordEncoder.encode("password"))
                .name("Gabor Farkas")
                .email("gabor.farkas@student.uni.hu")
                .program("Computer Science BSc")
                .faculty("Faculty of Engineering")
                .semester(8)
                .enrollmentDate(LocalDate.of(2021, 2, 1))
                .birthDate(LocalDate.of(2000, 9, 14))
                .phoneNumber("+36307890123")
                .address("Pecs, Hungary")
                .status("ACTIVE")
                .gpa(3.7)
                .completedCredits(175)
                .requiredCredits(180)
                .build(),

            Student.builder()
                .id(studentIdCounter++)
                .neptunCode("VWX234")
                .password(passwordEncoder.encode("password"))
                .name("Katalin Molnar")
                .email("katalin.molnar@student.uni.hu")
                .program("Information Technology BSc")
                .faculty("Faculty of Engineering")
                .semester(5)
                .enrollmentDate(LocalDate.of(2022, 9, 1))
                .birthDate(LocalDate.of(2002, 4, 8))
                .phoneNumber("+36308901234")
                .address("Gyor, Hungary")
                .status("ACTIVE")
                .gpa(4.3)
                .completedCredits(115)
                .requiredCredits(180)
                .build(),

            Student.builder()
                .id(studentIdCounter++)
                .neptunCode("YZA567")
                .password(passwordEncoder.encode("password"))
                .name("Laszlo Varga")
                .email("laszlo.varga@student.uni.hu")
                .program("Software Engineering BSc")
                .faculty("Faculty of Engineering")
                .semester(3)
                .enrollmentDate(LocalDate.of(2023, 9, 1))
                .birthDate(LocalDate.of(2003, 12, 20))
                .phoneNumber("+36309012345")
                .address("Miskolc, Hungary")
                .status("ACTIVE")
                .gpa(4.0)
                .completedCredits(65)
                .requiredCredits(180)
                .build(),

            Student.builder()
                .id(studentIdCounter++)
                .neptunCode("BCD890")
                .password(passwordEncoder.encode("password"))
                .name("Zsofia Nemeth")
                .email("zsofia.nemeth@student.uni.hu")
                .program("Computer Science BSc")
                .faculty("Faculty of Engineering")
                .semester(4)
                .enrollmentDate(LocalDate.of(2023, 2, 1))
                .birthDate(LocalDate.of(2003, 3, 17))
                .phoneNumber("+36300123456")
                .address("Budapest, Hungary")
                .status("ACTIVE")
                .gpa(4.4)
                .completedCredits(95)
                .requiredCredits(180)
                .build(),

            Student.builder()
                .id(studentIdCounter++)
                .neptunCode("EFG123")
                .password(passwordEncoder.encode("password"))
                .name("Tamas Kovacs")
                .email("tamas.kovacs@student.uni.hu")
                .program("Software Engineering BSc")
                .faculty("Faculty of Engineering")
                .semester(6)
                .enrollmentDate(LocalDate.of(2022, 2, 1))
                .birthDate(LocalDate.of(2001, 10, 29))
                .phoneNumber("+36301234578")
                .address("Budapest, Hungary")
                .status("ACTIVE")
                .gpa(3.6)
                .completedCredits(145)
                .requiredCredits(180)
                .build()
        );

        studentList.forEach(student -> {
            students.put(student.getId(), student);
            studentsByNeptunCode.put(student.getNeptunCode(), student);
        });
    }

    private void createCourses() {
        List<Course> courseList = Arrays.asList(
            Course.builder()
                .id(courseIdCounter++)
                .courseCode("CS101")
                .name("Introduction to Programming")
                .credits(5)
                .instructor("Dr. Anna Kovacs")
                .instructorEmail("anna.kovacs@uni.hu")
                .department("Computer Science")
                .semester("2024/2025 Fall")
                .type("Lecture")
                .description("Introduction to programming concepts using Java")
                .syllabus("Week 1-2: Variables and Data Types, Week 3-4: Control Structures...")
                .prerequisites(Arrays.asList())
                .assessmentMethod("Written Exam (60%), Projects (40%)")
                .maxStudents(100)
                .enrolledStudents(85)
                .language("English")
                .location("Building A")
                .build(),

            Course.builder()
                .id(courseIdCounter++)
                .courseCode("CS201")
                .name("Data Structures and Algorithms")
                .credits(6)
                .instructor("Dr. Janos Toth")
                .instructorEmail("janos.toth@uni.hu")
                .department("Computer Science")
                .semester("2024/2025 Fall")
                .type("Lecture")
                .description("Advanced data structures and algorithm design")
                .syllabus("Week 1-3: Arrays and Linked Lists, Week 4-6: Trees and Graphs...")
                .prerequisites(Arrays.asList("CS101"))
                .assessmentMethod("Written Exam (50%), Programming Assignments (50%)")
                .maxStudents(80)
                .enrolledStudents(72)
                .language("English")
                .location("Building B")
                .build(),

            Course.builder()
                .id(courseIdCounter++)
                .courseCode("CS301")
                .name("Database Systems")
                .credits(5)
                .instructor("Dr. Eva Szabo")
                .instructorEmail("eva.szabo@uni.hu")
                .department("Computer Science")
                .semester("2024/2025 Fall")
                .type("Lecture")
                .description("Relational database design and SQL")
                .syllabus("Week 1-2: ER Modeling, Week 3-5: SQL Basics...")
                .prerequisites(Arrays.asList("CS101"))
                .assessmentMethod("Written Exam (60%), Project (40%)")
                .maxStudents(90)
                .enrolledStudents(78)
                .language("English")
                .location("Building A")
                .build(),

            Course.builder()
                .id(courseIdCounter++)
                .courseCode("MATH101")
                .name("Calculus I")
                .credits(4)
                .instructor("Dr. Maria Kiss")
                .instructorEmail("maria.kiss@uni.hu")
                .department("Mathematics")
                .semester("2024/2025 Fall")
                .type("Lecture")
                .description("Differential and integral calculus")
                .syllabus("Week 1-4: Limits and Continuity, Week 5-8: Derivatives...")
                .prerequisites(Arrays.asList())
                .assessmentMethod("Written Exam (100%)")
                .maxStudents(150)
                .enrolledStudents(142)
                .language("Hungarian")
                .location("Building C")
                .build(),

            Course.builder()
                .id(courseIdCounter++)
                .courseCode("CS401")
                .name("Software Engineering")
                .credits(6)
                .instructor("Dr. Peter Horvath")
                .instructorEmail("peter.horvath@uni.hu")
                .department("Computer Science")
                .semester("2024/2025 Fall")
                .type("Lecture")
                .description("Software development methodologies and practices")
                .syllabus("Week 1-3: SDLC, Week 4-6: Agile & Scrum, Week 7-9: Design Patterns...")
                .prerequisites(Arrays.asList("CS201"))
                .assessmentMethod("Written Exam (40%), Team Project (60%)")
                .maxStudents(70)
                .enrolledStudents(65)
                .language("English")
                .location("Building B")
                .build(),

            Course.builder()
                .id(courseIdCounter++)
                .courseCode("CS202")
                .name("Operating Systems")
                .credits(5)
                .instructor("Dr. Gabor Nagy")
                .instructorEmail("gabor.nagy@uni.hu")
                .department("Computer Science")
                .semester("2024/2025 Fall")
                .type("Lecture")
                .description("Operating system concepts and implementation")
                .syllabus("Week 1-2: Processes and Threads, Week 3-5: Memory Management...")
                .prerequisites(Arrays.asList("CS101"))
                .assessmentMethod("Written Exam (70%), Lab Work (30%)")
                .maxStudents(85)
                .enrolledStudents(79)
                .language("English")
                .location("Building A")
                .build(),

            Course.builder()
                .id(courseIdCounter++)
                .courseCode("CS302")
                .name("Computer Networks")
                .credits(5)
                .instructor("Dr. Katalin Farkas")
                .instructorEmail("katalin.farkas@uni.hu")
                .department("Computer Science")
                .semester("2024/2025 Fall")
                .type("Lecture")
                .description("Network protocols and architecture")
                .syllabus("Week 1-2: OSI Model, Week 3-5: TCP/IP, Week 6-8: Network Security...")
                .prerequisites(Arrays.asList("CS201"))
                .assessmentMethod("Written Exam (60%), Labs (40%)")
                .maxStudents(75)
                .enrolledStudents(68)
                .language("English")
                .location("Building B")
                .build(),

            Course.builder()
                .id(courseIdCounter++)
                .courseCode("MATH201")
                .name("Linear Algebra")
                .credits(4)
                .instructor("Dr. Zoltan Varga")
                .instructorEmail("zoltan.varga@uni.hu")
                .department("Mathematics")
                .semester("2024/2025 Fall")
                .type("Lecture")
                .description("Vectors, matrices, and linear transformations")
                .syllabus("Week 1-3: Vector Spaces, Week 4-6: Matrices, Week 7-9: Eigenvalues...")
                .prerequisites(Arrays.asList("MATH101"))
                .assessmentMethod("Written Exam (100%)")
                .maxStudents(120)
                .enrolledStudents(110)
                .language("Hungarian")
                .location("Building C")
                .build(),

            Course.builder()
                .id(courseIdCounter++)
                .courseCode("CS501")
                .name("Artificial Intelligence")
                .credits(6)
                .instructor("Dr. Laszlo Nemeth")
                .instructorEmail("laszlo.nemeth@uni.hu")
                .department("Computer Science")
                .semester("2024/2025 Fall")
                .type("Lecture")
                .description("Introduction to AI concepts and machine learning")
                .syllabus("Week 1-3: Search Algorithms, Week 4-6: ML Basics, Week 7-10: Neural Networks...")
                .prerequisites(Arrays.asList("CS201", "MATH201"))
                .assessmentMethod("Written Exam (50%), Project (50%)")
                .maxStudents(60)
                .enrolledStudents(58)
                .language("English")
                .location("Building A")
                .build(),

            Course.builder()
                .id(courseIdCounter++)
                .courseCode("CS303")
                .name("Web Development")
                .credits(5)
                .instructor("Dr. Eva Molnar")
                .instructorEmail("eva.molnar@uni.hu")
                .department("Computer Science")
                .semester("2024/2025 Fall")
                .type("Lecture")
                .description("Modern web development with HTML, CSS, JavaScript")
                .syllabus("Week 1-2: HTML/CSS, Week 3-5: JavaScript, Week 6-8: React...")
                .prerequisites(Arrays.asList("CS101"))
                .assessmentMethod("Project (100%)")
                .maxStudents(95)
                .enrolledStudents(89)
                .language("English")
                .location("Building B")
                .build()
        );

        courseList.forEach(course -> {
            courses.put(course.getId(), course);
            coursesByCode.put(course.getCourseCode(), course);
        });
    }

    private void createEnrollments() {
        enrollments.put(enrollmentIdCounter, Enrollment.builder()
                .id(enrollmentIdCounter++).studentId(1L).courseId(1L).semester("2024/2025 Fall")
                .status("ACTIVE").enrollmentDate(LocalDate.of(2024, 9, 1)).attendance(85).build());
        enrollments.put(enrollmentIdCounter, Enrollment.builder()
                .id(enrollmentIdCounter++).studentId(1L).courseId(2L).semester("2024/2025 Fall")
                .status("ACTIVE").enrollmentDate(LocalDate.of(2024, 9, 1)).attendance(90).build());
        enrollments.put(enrollmentIdCounter, Enrollment.builder()
                .id(enrollmentIdCounter++).studentId(1L).courseId(3L).semester("2024/2025 Fall")
                .status("ACTIVE").enrollmentDate(LocalDate.of(2024, 9, 1)).attendance(88).build());
        enrollments.put(enrollmentIdCounter, Enrollment.builder()
                .id(enrollmentIdCounter++).studentId(1L).courseId(5L).semester("2024/2025 Fall")
                .status("ACTIVE").enrollmentDate(LocalDate.of(2024, 9, 1)).attendance(82).build());

        enrollments.put(enrollmentIdCounter, Enrollment.builder()
                .id(enrollmentIdCounter++).studentId(2L).courseId(1L).semester("2024/2025 Fall")
                .status("ACTIVE").enrollmentDate(LocalDate.of(2024, 9, 1)).attendance(95).build());
        enrollments.put(enrollmentIdCounter, Enrollment.builder()
                .id(enrollmentIdCounter++).studentId(2L).courseId(4L).semester("2024/2025 Fall")
                .status("ACTIVE").enrollmentDate(LocalDate.of(2024, 9, 1)).attendance(92).build());
        enrollments.put(enrollmentIdCounter, Enrollment.builder()
                .id(enrollmentIdCounter++).studentId(2L).courseId(10L).semester("2024/2025 Fall")
                .status("ACTIVE").enrollmentDate(LocalDate.of(2024, 9, 1)).attendance(89).build());

        enrollments.put(enrollmentIdCounter, Enrollment.builder()
                .id(enrollmentIdCounter++).studentId(3L).courseId(2L).semester("2024/2025 Fall")
                .status("ACTIVE").enrollmentDate(LocalDate.of(2024, 9, 1)).attendance(78).build());
        enrollments.put(enrollmentIdCounter, Enrollment.builder()
                .id(enrollmentIdCounter++).studentId(3L).courseId(3L).semester("2024/2025 Fall")
                .status("ACTIVE").enrollmentDate(LocalDate.of(2024, 9, 1)).attendance(85).build());
        enrollments.put(enrollmentIdCounter, Enrollment.builder()
                .id(enrollmentIdCounter++).studentId(3L).courseId(5L).semester("2024/2025 Fall")
                .status("ACTIVE").enrollmentDate(LocalDate.of(2024, 9, 1)).attendance(80).build());
        enrollments.put(enrollmentIdCounter, Enrollment.builder()
                .id(enrollmentIdCounter++).studentId(3L).courseId(9L).semester("2024/2025 Fall")
                .status("ACTIVE").enrollmentDate(LocalDate.of(2024, 9, 1)).attendance(83).build());

        enrollments.put(enrollmentIdCounter, Enrollment.builder()
                .id(enrollmentIdCounter++).studentId(4L).courseId(1L).semester("2024/2025 Fall")
                .status("ACTIVE").enrollmentDate(LocalDate.of(2024, 9, 1)).attendance(90).build());
        enrollments.put(enrollmentIdCounter, Enrollment.builder()
                .id(enrollmentIdCounter++).studentId(4L).courseId(2L).semester("2024/2025 Fall")
                .status("ACTIVE").enrollmentDate(LocalDate.of(2024, 9, 1)).attendance(87).build());
        enrollments.put(enrollmentIdCounter, Enrollment.builder()
                .id(enrollmentIdCounter++).studentId(4L).courseId(3L).semester("2024/2025 Fall")
                .status("ACTIVE").enrollmentDate(LocalDate.of(2024, 9, 1)).attendance(92).build());
        enrollments.put(enrollmentIdCounter, Enrollment.builder()
                .id(enrollmentIdCounter++).studentId(4L).courseId(6L).semester("2024/2025 Fall")
                .status("ACTIVE").enrollmentDate(LocalDate.of(2024, 9, 1)).attendance(88).build());

        enrollments.put(enrollmentIdCounter, Enrollment.builder()
                .id(enrollmentIdCounter++).studentId(5L).courseId(1L).semester("2024/2025 Fall")
                .status("ACTIVE").enrollmentDate(LocalDate.of(2024, 9, 1)).attendance(93).build());
        enrollments.put(enrollmentIdCounter, Enrollment.builder()
                .id(enrollmentIdCounter++).studentId(5L).courseId(3L).semester("2024/2025 Fall")
                .status("ACTIVE").enrollmentDate(LocalDate.of(2024, 9, 1)).attendance(91).build());
        enrollments.put(enrollmentIdCounter, Enrollment.builder()
                .id(enrollmentIdCounter++).studentId(5L).courseId(4L).semester("2024/2025 Fall")
                .status("ACTIVE").enrollmentDate(LocalDate.of(2024, 9, 1)).attendance(94).build());
        enrollments.put(enrollmentIdCounter, Enrollment.builder()
                .id(enrollmentIdCounter++).studentId(5L).courseId(10L).semester("2024/2025 Fall")
                .status("ACTIVE").enrollmentDate(LocalDate.of(2024, 9, 1)).attendance(89).build());

        enrollments.put(enrollmentIdCounter, Enrollment.builder()
                .id(enrollmentIdCounter++).studentId(6L).courseId(2L).semester("2024/2025 Fall")
                .status("ACTIVE").enrollmentDate(LocalDate.of(2024, 9, 1)).attendance(84).build());
        enrollments.put(enrollmentIdCounter, Enrollment.builder()
                .id(enrollmentIdCounter++).studentId(6L).courseId(3L).semester("2024/2025 Fall")
                .status("ACTIVE").enrollmentDate(LocalDate.of(2024, 9, 1)).attendance(86).build());
        enrollments.put(enrollmentIdCounter, Enrollment.builder()
                .id(enrollmentIdCounter++).studentId(6L).courseId(5L).semester("2024/2025 Fall")
                .status("ACTIVE").enrollmentDate(LocalDate.of(2024, 9, 1)).attendance(81).build());
        enrollments.put(enrollmentIdCounter, Enrollment.builder()
                .id(enrollmentIdCounter++).studentId(6L).courseId(6L).semester("2024/2025 Fall")
                .status("ACTIVE").enrollmentDate(LocalDate.of(2024, 9, 1)).attendance(83).build());

        enrollments.put(enrollmentIdCounter, Enrollment.builder()
                .id(enrollmentIdCounter++).studentId(7L).courseId(1L).semester("2024/2025 Fall")
                .status("ACTIVE").enrollmentDate(LocalDate.of(2024, 9, 1)).attendance(97).build());
        enrollments.put(enrollmentIdCounter, Enrollment.builder()
                .id(enrollmentIdCounter++).studentId(7L).courseId(4L).semester("2024/2025 Fall")
                .status("ACTIVE").enrollmentDate(LocalDate.of(2024, 9, 1)).attendance(96).build());
        enrollments.put(enrollmentIdCounter, Enrollment.builder()
                .id(enrollmentIdCounter++).studentId(7L).courseId(10L).semester("2024/2025 Fall")
                .status("ACTIVE").enrollmentDate(LocalDate.of(2024, 9, 1)).attendance(95).build());

        enrollments.put(enrollmentIdCounter, Enrollment.builder()
                .id(enrollmentIdCounter++).studentId(8L).courseId(5L).semester("2024/2025 Fall")
                .status("ACTIVE").enrollmentDate(LocalDate.of(2024, 9, 1)).attendance(75).build());
        enrollments.put(enrollmentIdCounter, Enrollment.builder()
                .id(enrollmentIdCounter++).studentId(8L).courseId(7L).semester("2024/2025 Fall")
                .status("ACTIVE").enrollmentDate(LocalDate.of(2024, 9, 1)).attendance(77).build());
        enrollments.put(enrollmentIdCounter, Enrollment.builder()
                .id(enrollmentIdCounter++).studentId(8L).courseId(9L).semester("2024/2025 Fall")
                .status("ACTIVE").enrollmentDate(LocalDate.of(2024, 9, 1)).attendance(79).build());

        enrollments.put(enrollmentIdCounter, Enrollment.builder()
                .id(enrollmentIdCounter++).studentId(9L).courseId(2L).semester("2024/2025 Fall")
                .status("ACTIVE").enrollmentDate(LocalDate.of(2024, 9, 1)).attendance(91).build());
        enrollments.put(enrollmentIdCounter, Enrollment.builder()
                .id(enrollmentIdCounter++).studentId(9L).courseId(3L).semester("2024/2025 Fall")
                .status("ACTIVE").enrollmentDate(LocalDate.of(2024, 9, 1)).attendance(88).build());
        enrollments.put(enrollmentIdCounter, Enrollment.builder()
                .id(enrollmentIdCounter++).studentId(9L).courseId(6L).semester("2024/2025 Fall")
                .status("ACTIVE").enrollmentDate(LocalDate.of(2024, 9, 1)).attendance(90).build());
        enrollments.put(enrollmentIdCounter, Enrollment.builder()
                .id(enrollmentIdCounter++).studentId(9L).courseId(8L).semester("2024/2025 Fall")
                .status("ACTIVE").enrollmentDate(LocalDate.of(2024, 9, 1)).attendance(87).build());

        enrollments.put(enrollmentIdCounter, Enrollment.builder()
                .id(enrollmentIdCounter++).studentId(10L).courseId(1L).semester("2024/2025 Fall")
                .status("ACTIVE").enrollmentDate(LocalDate.of(2024, 9, 1)).attendance(86).build());
        enrollments.put(enrollmentIdCounter, Enrollment.builder()
                .id(enrollmentIdCounter++).studentId(10L).courseId(4L).semester("2024/2025 Fall")
                .status("ACTIVE").enrollmentDate(LocalDate.of(2024, 9, 1)).attendance(84).build());
        enrollments.put(enrollmentIdCounter, Enrollment.builder()
                .id(enrollmentIdCounter++).studentId(10L).courseId(10L).semester("2024/2025 Fall")
                .status("ACTIVE").enrollmentDate(LocalDate.of(2024, 9, 1)).attendance(88).build());

        enrollments.put(enrollmentIdCounter, Enrollment.builder()
                .id(enrollmentIdCounter++).studentId(11L).courseId(1L).semester("2024/2025 Fall")
                .status("ACTIVE").enrollmentDate(LocalDate.of(2024, 9, 1)).attendance(93).build());
        enrollments.put(enrollmentIdCounter, Enrollment.builder()
                .id(enrollmentIdCounter++).studentId(11L).courseId(2L).semester("2024/2025 Fall")
                .status("ACTIVE").enrollmentDate(LocalDate.of(2024, 9, 1)).attendance(91).build());
        enrollments.put(enrollmentIdCounter, Enrollment.builder()
                .id(enrollmentIdCounter++).studentId(11L).courseId(4L).semester("2024/2025 Fall")
                .status("ACTIVE").enrollmentDate(LocalDate.of(2024, 9, 1)).attendance(92).build());
        enrollments.put(enrollmentIdCounter, Enrollment.builder()
                .id(enrollmentIdCounter++).studentId(11L).courseId(10L).semester("2024/2025 Fall")
                .status("ACTIVE").enrollmentDate(LocalDate.of(2024, 9, 1)).attendance(94).build());

        enrollments.put(enrollmentIdCounter, Enrollment.builder()
                .id(enrollmentIdCounter++).studentId(12L).courseId(3L).semester("2024/2025 Fall")
                .status("ACTIVE").enrollmentDate(LocalDate.of(2024, 9, 1)).attendance(80).build());
        enrollments.put(enrollmentIdCounter, Enrollment.builder()
                .id(enrollmentIdCounter++).studentId(12L).courseId(5L).semester("2024/2025 Fall")
                .status("ACTIVE").enrollmentDate(LocalDate.of(2024, 9, 1)).attendance(78).build());
        enrollments.put(enrollmentIdCounter, Enrollment.builder()
                .id(enrollmentIdCounter++).studentId(12L).courseId(6L).semester("2024/2025 Fall")
                .status("ACTIVE").enrollmentDate(LocalDate.of(2024, 9, 1)).attendance(82).build());
        enrollments.put(enrollmentIdCounter, Enrollment.builder()
                .id(enrollmentIdCounter++).studentId(12L).courseId(7L).semester("2024/2025 Fall")
                .status("ACTIVE").enrollmentDate(LocalDate.of(2024, 9, 1)).attendance(81).build());
    }

    private void createTimetableEntries() {
        timetableEntries.put(timetableIdCounter, TimetableEntry.builder()
                .id(timetableIdCounter++).courseId(1L).courseCode("CS101")
                .courseName("Introduction to Programming").instructor("Dr. Anna Kovacs")
                .dayOfWeek(DayOfWeek.MONDAY).startTime(LocalTime.of(8, 0)).endTime(LocalTime.of(10, 0))
                .room("A301").building("Building A").type("Lecture").weekNumber(1).semester("2024/2025 Fall").build());
        timetableEntries.put(timetableIdCounter, TimetableEntry.builder()
                .id(timetableIdCounter++).courseId(1L).courseCode("CS101")
                .courseName("Introduction to Programming").instructor("Dr. Anna Kovacs")
                .dayOfWeek(DayOfWeek.WEDNESDAY).startTime(LocalTime.of(8, 0)).endTime(LocalTime.of(10, 0))
                .room("A301").building("Building A").type("Lab").weekNumber(1).semester("2024/2025 Fall").build());

        timetableEntries.put(timetableIdCounter, TimetableEntry.builder()
                .id(timetableIdCounter++).courseId(2L).courseCode("CS201")
                .courseName("Data Structures and Algorithms").instructor("Dr. Janos Toth")
                .dayOfWeek(DayOfWeek.TUESDAY).startTime(LocalTime.of(10, 0)).endTime(LocalTime.of(12, 0))
                .room("B205").building("Building B").type("Lecture").weekNumber(1).semester("2024/2025 Fall").build());
        timetableEntries.put(timetableIdCounter, TimetableEntry.builder()
                .id(timetableIdCounter++).courseId(2L).courseCode("CS201")
                .courseName("Data Structures and Algorithms").instructor("Dr. Janos Toth")
                .dayOfWeek(DayOfWeek.THURSDAY).startTime(LocalTime.of(10, 0)).endTime(LocalTime.of(12, 0))
                .room("B205").building("Building B").type("Lab").weekNumber(1).semester("2024/2025 Fall").build());

        timetableEntries.put(timetableIdCounter, TimetableEntry.builder()
                .id(timetableIdCounter++).courseId(3L).courseCode("CS301")
                .courseName("Database Systems").instructor("Dr. Eva Szabo")
                .dayOfWeek(DayOfWeek.WEDNESDAY).startTime(LocalTime.of(14, 0)).endTime(LocalTime.of(16, 0))
                .room("A201").building("Building A").type("Lecture").weekNumber(1).semester("2024/2025 Fall").build());
        timetableEntries.put(timetableIdCounter, TimetableEntry.builder()
                .id(timetableIdCounter++).courseId(3L).courseCode("CS301")
                .courseName("Database Systems").instructor("Dr. Eva Szabo")
                .dayOfWeek(DayOfWeek.FRIDAY).startTime(LocalTime.of(14, 0)).endTime(LocalTime.of(16, 0))
                .room("A201").building("Building A").type("Lab").weekNumber(1).semester("2024/2025 Fall").build());

        timetableEntries.put(timetableIdCounter, TimetableEntry.builder()
                .id(timetableIdCounter++).courseId(4L).courseCode("MATH101")
                .courseName("Calculus I").instructor("Dr. Maria Kiss")
                .dayOfWeek(DayOfWeek.MONDAY).startTime(LocalTime.of(10, 0)).endTime(LocalTime.of(12, 0))
                .room("C101").building("Building C").type("Lecture").weekNumber(1).semester("2024/2025 Fall").build());
        timetableEntries.put(timetableIdCounter, TimetableEntry.builder()
                .id(timetableIdCounter++).courseId(4L).courseCode("MATH101")
                .courseName("Calculus I").instructor("Dr. Maria Kiss")
                .dayOfWeek(DayOfWeek.THURSDAY).startTime(LocalTime.of(8, 0)).endTime(LocalTime.of(10, 0))
                .room("C101").building("Building C").type("Practice").weekNumber(1).semester("2024/2025 Fall").build());

        timetableEntries.put(timetableIdCounter, TimetableEntry.builder()
                .id(timetableIdCounter++).courseId(5L).courseCode("CS401")
                .courseName("Software Engineering").instructor("Dr. Peter Horvath")
                .dayOfWeek(DayOfWeek.TUESDAY).startTime(LocalTime.of(14, 0)).endTime(LocalTime.of(16, 0))
                .room("B301").building("Building B").type("Lecture").weekNumber(1).semester("2024/2025 Fall").build());
        timetableEntries.put(timetableIdCounter, TimetableEntry.builder()
                .id(timetableIdCounter++).courseId(5L).courseCode("CS401")
                .courseName("Software Engineering").instructor("Dr. Peter Horvath")
                .dayOfWeek(DayOfWeek.FRIDAY).startTime(LocalTime.of(10, 0)).endTime(LocalTime.of(12, 0))
                .room("B301").building("Building B").type("Seminar").weekNumber(1).semester("2024/2025 Fall").build());

        timetableEntries.put(timetableIdCounter, TimetableEntry.builder()
                .id(timetableIdCounter++).courseId(6L).courseCode("CS202")
                .courseName("Operating Systems").instructor("Dr. Gabor Nagy")
                .dayOfWeek(DayOfWeek.MONDAY).startTime(LocalTime.of(14, 0)).endTime(LocalTime.of(16, 0))
                .room("A305").building("Building A").type("Lecture").weekNumber(1).semester("2024/2025 Fall").build());
        timetableEntries.put(timetableIdCounter, TimetableEntry.builder()
                .id(timetableIdCounter++).courseId(6L).courseCode("CS202")
                .courseName("Operating Systems").instructor("Dr. Gabor Nagy")
                .dayOfWeek(DayOfWeek.WEDNESDAY).startTime(LocalTime.of(10, 0)).endTime(LocalTime.of(12, 0))
                .room("A305").building("Building A").type("Lab").weekNumber(1).semester("2024/2025 Fall").build());

        timetableEntries.put(timetableIdCounter, TimetableEntry.builder()
                .id(timetableIdCounter++).courseId(7L).courseCode("CS302")
                .courseName("Computer Networks").instructor("Dr. Katalin Farkas")
                .dayOfWeek(DayOfWeek.TUESDAY).startTime(LocalTime.of(8, 0)).endTime(LocalTime.of(10, 0))
                .room("B201").building("Building B").type("Lecture").weekNumber(1).semester("2024/2025 Fall").build());
        timetableEntries.put(timetableIdCounter, TimetableEntry.builder()
                .id(timetableIdCounter++).courseId(7L).courseCode("CS302")
                .courseName("Computer Networks").instructor("Dr. Katalin Farkas")
                .dayOfWeek(DayOfWeek.THURSDAY).startTime(LocalTime.of(14, 0)).endTime(LocalTime.of(16, 0))
                .room("B201").building("Building B").type("Lab").weekNumber(1).semester("2024/2025 Fall").build());

        timetableEntries.put(timetableIdCounter, TimetableEntry.builder()
                .id(timetableIdCounter++).courseId(8L).courseCode("MATH201")
                .courseName("Linear Algebra").instructor("Dr. Zoltan Varga")
                .dayOfWeek(DayOfWeek.WEDNESDAY).startTime(LocalTime.of(10, 0)).endTime(LocalTime.of(12, 0))
                .room("C201").building("Building C").type("Lecture").weekNumber(1).semester("2024/2025 Fall").build());
        timetableEntries.put(timetableIdCounter, TimetableEntry.builder()
                .id(timetableIdCounter++).courseId(8L).courseCode("MATH201")
                .courseName("Linear Algebra").instructor("Dr. Zoltan Varga")
                .dayOfWeek(DayOfWeek.FRIDAY).startTime(LocalTime.of(8, 0)).endTime(LocalTime.of(10, 0))
                .room("C201").building("Building C").type("Practice").weekNumber(1).semester("2024/2025 Fall").build());

        timetableEntries.put(timetableIdCounter, TimetableEntry.builder()
                .id(timetableIdCounter++).courseId(9L).courseCode("CS501")
                .courseName("Artificial Intelligence").instructor("Dr. Laszlo Nemeth")
                .dayOfWeek(DayOfWeek.MONDAY).startTime(LocalTime.of(16, 0)).endTime(LocalTime.of(18, 0))
                .room("A401").building("Building A").type("Lecture").weekNumber(1).semester("2024/2025 Fall").build());
        timetableEntries.put(timetableIdCounter, TimetableEntry.builder()
                .id(timetableIdCounter++).courseId(9L).courseCode("CS501")
                .courseName("Artificial Intelligence").instructor("Dr. Laszlo Nemeth")
                .dayOfWeek(DayOfWeek.THURSDAY).startTime(LocalTime.of(16, 0)).endTime(LocalTime.of(18, 0))
                .room("A401").building("Building A").type("Lab").weekNumber(1).semester("2024/2025 Fall").build());

        timetableEntries.put(timetableIdCounter, TimetableEntry.builder()
                .id(timetableIdCounter++).courseId(10L).courseCode("CS303")
                .courseName("Web Development").instructor("Dr. Eva Molnar")
                .dayOfWeek(DayOfWeek.WEDNESDAY).startTime(LocalTime.of(16, 0)).endTime(LocalTime.of(18, 0))
                .room("B101").building("Building B").type("Lecture").weekNumber(1).semester("2024/2025 Fall").build());
        timetableEntries.put(timetableIdCounter, TimetableEntry.builder()
                .id(timetableIdCounter++).courseId(10L).courseCode("CS303")
                .courseName("Web Development").instructor("Dr. Eva Molnar")
                .dayOfWeek(DayOfWeek.FRIDAY).startTime(LocalTime.of(16, 0)).endTime(LocalTime.of(18, 0))
                .room("B101").building("Building B").type("Lab").weekNumber(1).semester("2024/2025 Fall").build());
    }

    private void createExams() {
        exams.put(examIdCounter, Exam.builder().id(examIdCounter++).courseId(1L).courseCode("CS101")
                .courseName("Introduction to Programming").examType("WRITTEN")
                .examDate(LocalDateTime.of(2025, 1, 15, 10, 0)).location("Building A").room("A Hall")
                .duration(120).instructor("Dr. Anna Kovacs").status("SCHEDULED")
                .maxStudents(100).registeredStudents(85).build());

        exams.put(examIdCounter, Exam.builder().id(examIdCounter++).courseId(2L).courseCode("CS201")
                .courseName("Data Structures and Algorithms").examType("WRITTEN")
                .examDate(LocalDateTime.of(2025, 1, 18, 14, 0)).location("Building B").room("B Hall")
                .duration(150).instructor("Dr. Janos Toth").status("SCHEDULED")
                .maxStudents(80).registeredStudents(72).build());

        exams.put(examIdCounter, Exam.builder().id(examIdCounter++).courseId(3L).courseCode("CS301")
                .courseName("Database Systems").examType("WRITTEN")
                .examDate(LocalDateTime.of(2025, 1, 20, 10, 0)).location("Building A").room("A Hall")
                .duration(120).instructor("Dr. Eva Szabo").status("SCHEDULED")
                .maxStudents(90).registeredStudents(78).build());

        exams.put(examIdCounter, Exam.builder().id(examIdCounter++).courseId(4L).courseCode("MATH101")
                .courseName("Calculus I").examType("WRITTEN")
                .examDate(LocalDateTime.of(2025, 1, 22, 9, 0)).location("Building C").room("C Hall")
                .duration(180).instructor("Dr. Maria Kiss").status("SCHEDULED")
                .maxStudents(150).registeredStudents(142).build());

        exams.put(examIdCounter, Exam.builder().id(examIdCounter++).courseId(5L).courseCode("CS401")
                .courseName("Software Engineering").examType("PROJECT")
                .examDate(LocalDateTime.of(2025, 1, 25, 14, 0)).location("Building B").room("B301")
                .duration(90).instructor("Dr. Peter Horvath").status("SCHEDULED")
                .maxStudents(70).registeredStudents(65).build());

        exams.put(examIdCounter, Exam.builder().id(examIdCounter++).courseId(6L).courseCode("CS202")
                .courseName("Operating Systems").examType("WRITTEN")
                .examDate(LocalDateTime.of(2025, 1, 23, 10, 0)).location("Building A").room("A Hall")
                .duration(150).instructor("Dr. Gabor Nagy").status("SCHEDULED")
                .maxStudents(85).registeredStudents(79).build());

        exams.put(examIdCounter, Exam.builder().id(examIdCounter++).courseId(7L).courseCode("CS302")
                .courseName("Computer Networks").examType("WRITTEN")
                .examDate(LocalDateTime.of(2025, 1, 27, 14, 0)).location("Building B").room("B Hall")
                .duration(120).instructor("Dr. Katalin Farkas").status("SCHEDULED")
                .maxStudents(75).registeredStudents(68).build());

        exams.put(examIdCounter, Exam.builder().id(examIdCounter++).courseId(8L).courseCode("MATH201")
                .courseName("Linear Algebra").examType("WRITTEN")
                .examDate(LocalDateTime.of(2025, 1, 29, 10, 0)).location("Building C").room("C Hall")
                .duration(150).instructor("Dr. Zoltan Varga").status("SCHEDULED")
                .maxStudents(120).registeredStudents(110).build());

        exams.put(examIdCounter, Exam.builder().id(examIdCounter++).courseId(9L).courseCode("CS501")
                .courseName("Artificial Intelligence").examType("PROJECT")
                .examDate(LocalDateTime.of(2025, 1, 30, 16, 0)).location("Building A").room("A401")
                .duration(120).instructor("Dr. Laszlo Nemeth").status("SCHEDULED")
                .maxStudents(60).registeredStudents(58).build());

        exams.put(examIdCounter, Exam.builder().id(examIdCounter++).courseId(10L).courseCode("CS303")
                .courseName("Web Development").examType("PROJECT")
                .examDate(LocalDateTime.of(2025, 2, 1, 14, 0)).location("Building B").room("B101")
                .duration(90).instructor("Dr. Eva Molnar").status("SCHEDULED")
                .maxStudents(95).registeredStudents(89).build());
    }

    private void createGrades() {
        grades.put(gradeIdCounter, Grade.builder().id(gradeIdCounter++).studentId(1L).courseId(4L)
                .courseCode("MATH101").courseName("Calculus I").credits(4).grade("A").gradeValue(5)
                .gradeDate(LocalDate.of(2024, 6, 15)).semester("2023/2024 Spring")
                .instructor("Dr. Maria Kiss").examType("WRITTEN").build());
        grades.put(gradeIdCounter, Grade.builder().id(gradeIdCounter++).studentId(1L).courseId(1L)
                .courseCode("CS101").courseName("Introduction to Programming").credits(5).grade("A").gradeValue(5)
                .gradeDate(LocalDate.of(2024, 12, 20)).semester("2024/2025 Fall")
                .instructor("Dr. Anna Kovacs").examType("WRITTEN").build());

        grades.put(gradeIdCounter, Grade.builder().id(gradeIdCounter++).studentId(2L).courseId(4L)
                .courseCode("MATH101").courseName("Calculus I").credits(4).grade("A+").gradeValue(5)
                .gradeDate(LocalDate.of(2024, 6, 15)).semester("2023/2024 Spring")
                .instructor("Dr. Maria Kiss").examType("WRITTEN").build());

        grades.put(gradeIdCounter, Grade.builder().id(gradeIdCounter++).studentId(3L).courseId(1L)
                .courseCode("CS101").courseName("Introduction to Programming").credits(5).grade("B").gradeValue(4)
                .gradeDate(LocalDate.of(2023, 12, 18)).semester("2023/2024 Fall")
                .instructor("Dr. Anna Kovacs").examType("WRITTEN").build());
        grades.put(gradeIdCounter, Grade.builder().id(gradeIdCounter++).studentId(3L).courseId(4L)
                .courseCode("MATH101").courseName("Calculus I").credits(4).grade("B").gradeValue(4)
                .gradeDate(LocalDate.of(2024, 6, 15)).semester("2023/2024 Spring")
                .instructor("Dr. Maria Kiss").examType("WRITTEN").build());

        grades.put(gradeIdCounter, Grade.builder().id(gradeIdCounter++).studentId(4L).courseId(4L)
                .courseCode("MATH101").courseName("Calculus I").credits(4).grade("B+").gradeValue(4)
                .gradeDate(LocalDate.of(2024, 6, 15)).semester("2023/2024 Spring")
                .instructor("Dr. Maria Kiss").examType("WRITTEN").build());
        grades.put(gradeIdCounter, Grade.builder().id(gradeIdCounter++).studentId(4L).courseId(1L)
                .courseCode("CS101").courseName("Introduction to Programming").credits(5).grade("A").gradeValue(5)
                .gradeDate(LocalDate.of(2024, 12, 20)).semester("2024/2025 Fall")
                .instructor("Dr. Anna Kovacs").examType("WRITTEN").build());

        grades.put(gradeIdCounter, Grade.builder().id(gradeIdCounter++).studentId(5L).courseId(4L)
                .courseCode("MATH101").courseName("Calculus I").credits(4).grade("A").gradeValue(5)
                .gradeDate(LocalDate.of(2024, 6, 15)).semester("2023/2024 Spring")
                .instructor("Dr. Maria Kiss").examType("WRITTEN").build());

        grades.put(gradeIdCounter, Grade.builder().id(gradeIdCounter++).studentId(6L).courseId(1L)
                .courseCode("CS101").courseName("Introduction to Programming").credits(5).grade("B+").gradeValue(4)
                .gradeDate(LocalDate.of(2023, 12, 18)).semester("2023/2024 Fall")
                .instructor("Dr. Anna Kovacs").examType("WRITTEN").build());
        grades.put(gradeIdCounter, Grade.builder().id(gradeIdCounter++).studentId(6L).courseId(4L)
                .courseCode("MATH101").courseName("Calculus I").credits(4).grade("B").gradeValue(4)
                .gradeDate(LocalDate.of(2024, 6, 15)).semester("2023/2024 Spring")
                .instructor("Dr. Maria Kiss").examType("WRITTEN").build());

        grades.put(gradeIdCounter, Grade.builder().id(gradeIdCounter++).studentId(7L).courseId(4L)
                .courseCode("MATH101").courseName("Calculus I").credits(4).grade("A+").gradeValue(5)
                .gradeDate(LocalDate.of(2024, 6, 15)).semester("2023/2024 Spring")
                .instructor("Dr. Maria Kiss").examType("WRITTEN").build());

        grades.put(gradeIdCounter, Grade.builder().id(gradeIdCounter++).studentId(8L).courseId(1L)
                .courseCode("CS101").courseName("Introduction to Programming").credits(5).grade("B-").gradeValue(3)
                .gradeDate(LocalDate.of(2022, 12, 20)).semester("2022/2023 Fall")
                .instructor("Dr. Anna Kovacs").examType("WRITTEN").build());
        grades.put(gradeIdCounter, Grade.builder().id(gradeIdCounter++).studentId(8L).courseId(2L)
                .courseCode("CS201").courseName("Data Structures and Algorithms").credits(6).grade("B").gradeValue(4)
                .gradeDate(LocalDate.of(2023, 6, 18)).semester("2022/2023 Spring")
                .instructor("Dr. Janos Toth").examType("WRITTEN").build());
        grades.put(gradeIdCounter, Grade.builder().id(gradeIdCounter++).studentId(8L).courseId(3L)
                .courseCode("CS301").courseName("Database Systems").credits(5).grade("B+").gradeValue(4)
                .gradeDate(LocalDate.of(2023, 12, 22)).semester("2023/2024 Fall")
                .instructor("Dr. Eva Szabo").examType("WRITTEN").build());

        grades.put(gradeIdCounter, Grade.builder().id(gradeIdCounter++).studentId(9L).courseId(1L)
                .courseCode("CS101").courseName("Introduction to Programming").credits(5).grade("A").gradeValue(5)
                .gradeDate(LocalDate.of(2023, 12, 18)).semester("2023/2024 Fall")
                .instructor("Dr. Anna Kovacs").examType("WRITTEN").build());
        grades.put(gradeIdCounter, Grade.builder().id(gradeIdCounter++).studentId(9L).courseId(4L)
                .courseCode("MATH101").courseName("Calculus I").credits(4).grade("A-").gradeValue(5)
                .gradeDate(LocalDate.of(2024, 6, 15)).semester("2023/2024 Spring")
                .instructor("Dr. Maria Kiss").examType("WRITTEN").build());

        grades.put(gradeIdCounter, Grade.builder().id(gradeIdCounter++).studentId(10L).courseId(4L)
                .courseCode("MATH101").courseName("Calculus I").credits(4).grade("B+").gradeValue(4)
                .gradeDate(LocalDate.of(2024, 6, 15)).semester("2023/2024 Spring")
                .instructor("Dr. Maria Kiss").examType("WRITTEN").build());

        grades.put(gradeIdCounter, Grade.builder().id(gradeIdCounter++).studentId(11L).courseId(4L)
                .courseCode("MATH101").courseName("Calculus I").credits(4).grade("A").gradeValue(5)
                .gradeDate(LocalDate.of(2024, 6, 15)).semester("2023/2024 Spring")
                .instructor("Dr. Maria Kiss").examType("WRITTEN").build());

        grades.put(gradeIdCounter, Grade.builder().id(gradeIdCounter++).studentId(12L).courseId(1L)
                .courseCode("CS101").courseName("Introduction to Programming").credits(5).grade("B").gradeValue(4)
                .gradeDate(LocalDate.of(2023, 12, 18)).semester("2023/2024 Fall")
                .instructor("Dr. Anna Kovacs").examType("WRITTEN").build());
        grades.put(gradeIdCounter, Grade.builder().id(gradeIdCounter++).studentId(12L).courseId(2L)
                .courseCode("CS201").courseName("Data Structures and Algorithms").credits(6).grade("B-").gradeValue(3)
                .gradeDate(LocalDate.of(2024, 6, 18)).semester("2023/2024 Spring")
                .instructor("Dr. Janos Toth").examType("WRITTEN").build());
        grades.put(gradeIdCounter, Grade.builder().id(gradeIdCounter++).studentId(12L).courseId(4L)
                .courseCode("MATH101").courseName("Calculus I").credits(4).grade("C+").gradeValue(3)
                .gradeDate(LocalDate.of(2024, 6, 15)).semester("2023/2024 Spring")
                .instructor("Dr. Maria Kiss").examType("WRITTEN").build());
    }

    public Optional<Student> findStudentByNeptunCode(String neptunCode) {
        return Optional.ofNullable(studentsByNeptunCode.get(neptunCode));
    }

    public Optional<Student> findStudentById(Long id) {
        return Optional.ofNullable(students.get(id));
    }

    public List<Student> findAllStudents() {
        return new ArrayList<>(students.values());
    }

    public Optional<Course> findCourseByCode(String courseCode) {
        return Optional.ofNullable(coursesByCode.get(courseCode));
    }

    public Optional<Course> findCourseById(Long id) {
        return Optional.ofNullable(courses.get(id));
    }

    public List<Course> findAllCourses() {
        return new ArrayList<>(courses.values());
    }

    public List<Enrollment> findEnrollmentsByStudentId(Long studentId) {
        return enrollments.values().stream()
            .filter(e -> e.getStudentId().equals(studentId))
            .collect(Collectors.toList());
    }

    public List<Enrollment> findEnrollmentsByCourseId(Long courseId) {
        return enrollments.values().stream()
            .filter(e -> e.getCourseId().equals(courseId))
            .collect(Collectors.toList());
    }

    public List<TimetableEntry> findTimetableEntriesByStudentId(Long studentId) {
        List<Enrollment> studentEnrollments = findEnrollmentsByStudentId(studentId);
        Set<Long> courseIds = studentEnrollments.stream()
            .map(Enrollment::getCourseId)
            .collect(Collectors.toSet());

        return timetableEntries.values().stream()
            .filter(entry -> courseIds.contains(entry.getCourseId()))
            .collect(Collectors.toList());
    }

    public List<Exam> findUpcomingExams() {
        LocalDateTime now = LocalDateTime.now();
        return exams.values().stream()
            .filter(exam -> exam.getExamDate().isAfter(now))
            .sorted(Comparator.comparing(Exam::getExamDate))
            .collect(Collectors.toList());
    }

    public Optional<Exam> findExamById(Long id) {
        return Optional.ofNullable(exams.get(id));
    }

    public List<Grade> findGradesByStudentId(Long studentId) {
        return grades.values().stream()
            .filter(grade -> grade.getStudentId().equals(studentId))
            .collect(Collectors.toList());
    }

    public List<Grade> findGradesByStudentIdAndSemester(
        Long studentId,
        String semester
    ) {
        return grades.values().stream()
            .filter(grade -> grade.getStudentId().equals(studentId))
            .filter(grade -> grade.getSemester().equals(semester))
            .collect(Collectors.toList());
    }

}
