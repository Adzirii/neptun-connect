package com.thesis.neptunmock.dto.timetable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * TimetableEntryDto.java
 * Single timetable entry representing a class session
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TimetableEntryDto {

    private Long id;
    private String courseCode;
    private String courseName;
    private String instructor;
    private DayOfWeek dayOfWeek;
    private LocalTime startTime;
    private LocalTime endTime;
    private String room;
    private String building;
    private String type;
    private Integer weekNumber;
    private LocalDate date;
}
