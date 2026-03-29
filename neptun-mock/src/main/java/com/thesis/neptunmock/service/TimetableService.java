package com.thesis.neptunmock.service;

import com.thesis.neptunmock.dto.timetable.*;
import com.thesis.neptunmock.model.Student;
import com.thesis.neptunmock.model.TimetableEntry;
import com.thesis.neptunmock.repository.MockDataRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TimetableService {

    private final MockDataRepository repository;

    public WeeklyTimetableDto getCurrentTimetable(String neptunCode) {
        log.debug("Getting current timetable for student: {}", neptunCode);

        Student student = repository.findStudentByNeptunCode(neptunCode)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        List<TimetableEntry> entries = repository.findTimetableEntriesByStudentId(student.getId());

        LocalDate now = LocalDate.now();
        LocalDate weekStart = now.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate weekEnd = now.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

        return WeeklyTimetableDto.builder()
                .weekNumber(1)
                .weekStartDate(weekStart)
                .weekEndDate(weekEnd)
                .semester("2024/2025 Fall")
                .entries(entries.stream().map(this::mapToDto).collect(Collectors.toList()))
                .build();
    }

    public DailyTimetableDto getDailyTimetable(String neptunCode, LocalDate date) {
        log.debug("Getting daily timetable for student: {} on date: {}", neptunCode, date);

        Student student = repository.findStudentByNeptunCode(neptunCode)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        List<TimetableEntry> allEntries = repository.findTimetableEntriesByStudentId(student.getId());

        DayOfWeek dayOfWeek = date.getDayOfWeek();
        List<TimetableEntry> dailyEntries = allEntries.stream()
                .filter(entry -> entry.getDayOfWeek() == dayOfWeek)
                .collect(Collectors.toList());

        return DailyTimetableDto.builder()
                .date(date)
                .dayOfWeek(dayOfWeek)
                .entries(dailyEntries.stream().map(this::mapToDto).collect(Collectors.toList()))
                .build();
    }

    public TimetableSummaryDto getTimetableSummary(String neptunCode) {
        log.debug("Getting timetable summary for student: {}", neptunCode);

        Student student = repository.findStudentByNeptunCode(neptunCode)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        List<TimetableEntry> entries = repository.findTimetableEntriesByStudentId(student.getId());

        List<String> courseCodes = entries.stream()
                .map(TimetableEntry::getCourseCode)
                .distinct()
                .collect(Collectors.toList());

        return TimetableSummaryDto.builder()
                .semester("2024/2025 Fall")
                .totalCourses(courseCodes.size())
                .totalWeeklyHours(entries.size() * 2)
                .courseCodes(courseCodes)
                .semesterStartDate(LocalDate.of(2024, 9, 1))
                .semesterEndDate(LocalDate.of(2025, 1, 31))
                .build();
    }

    private TimetableEntryDto mapToDto(TimetableEntry entry) {
        return TimetableEntryDto.builder()
                .id(entry.getId())
                .courseCode(entry.getCourseCode())
                .courseName(entry.getCourseName())
                .instructor(entry.getInstructor())
                .dayOfWeek(entry.getDayOfWeek())
                .startTime(entry.getStartTime())
                .endTime(entry.getEndTime())
                .room(entry.getRoom())
                .building(entry.getBuilding())
                .type(entry.getType())
                .weekNumber(entry.getWeekNumber())
                .build();
    }
}
