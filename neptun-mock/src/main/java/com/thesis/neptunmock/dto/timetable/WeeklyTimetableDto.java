package com.thesis.neptunmock.dto.timetable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

/**
 * WeeklyTimetableDto.java
 * Complete weekly timetable
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WeeklyTimetableDto {

    private Integer weekNumber;
    private LocalDate weekStartDate;
    private LocalDate weekEndDate;
    private String semester;
    private List<TimetableEntryDto> entries;
}
