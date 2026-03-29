package com.thesis.neptunmock.dto.timetable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

/**
 * DailyTimetableDto.java
 * Daily schedule
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DailyTimetableDto {

    private LocalDate date;
    private DayOfWeek dayOfWeek;
    private List<TimetableEntryDto> entries;
}
