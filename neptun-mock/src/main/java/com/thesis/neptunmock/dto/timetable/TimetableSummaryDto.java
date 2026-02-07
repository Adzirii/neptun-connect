package com.thesis.neptunmock.dto.timetable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

/**
 * TimetableSummaryDto.java
 * Summary of timetable for a semester
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TimetableSummaryDto {

    private String semester;
    private Integer totalCourses;
    private Integer totalWeeklyHours;
    private List<String> courseCodes;
    private LocalDate semesterStartDate;
    private LocalDate semesterEndDate;
}