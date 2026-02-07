package com.thesis.neptunmock.controller;

import com.thesis.neptunmock.dto.timetable.*;
import com.thesis.neptunmock.service.TimetableService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

/**
 * TimetableController.java
 * REST Controller for timetable endpoints
 */
@RestController
@RequestMapping("/api/timetable")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Timetable", description = "Timetable management APIs")
@SecurityRequirement(name = "Bearer Authentication")
public class TimetableController {

    private final TimetableService timetableService;

    @Operation(summary = "Get current timetable", description = "Get current week timetable for authenticated student")
    @GetMapping("/current")
    public ResponseEntity<WeeklyTimetableDto> getCurrentTimetable(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        log.info("Request for current timetable: {}", userDetails.getUsername());
        WeeklyTimetableDto timetable = timetableService.getCurrentTimetable(userDetails.getUsername());
        return ResponseEntity.ok(timetable);
    }

    @Operation(summary = "Get weekly timetable", description = "Get weekly timetable (alias for current)")
    @GetMapping("/week")
    public ResponseEntity<WeeklyTimetableDto> getWeeklyTimetable(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        log.info("Request for weekly timetable: {}", userDetails.getUsername());
        WeeklyTimetableDto timetable = timetableService.getCurrentTimetable(userDetails.getUsername());
        return ResponseEntity.ok(timetable);
    }

    @Operation(summary = "Get daily timetable", description = "Get timetable for a specific day")
    @GetMapping("/day/{date}")
    public ResponseEntity<DailyTimetableDto> getDailyTimetable(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        log.info("Request for daily timetable: {} on {}", userDetails.getUsername(), date);
        DailyTimetableDto timetable = timetableService.getDailyTimetable(userDetails.getUsername(), date);
        return ResponseEntity.ok(timetable);
    }

    @Operation(summary = "Get timetable summary", description = "Get semester timetable summary")
    @GetMapping("/summary")
    public ResponseEntity<TimetableSummaryDto> getTimetableSummary(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        log.info("Request for timetable summary: {}", userDetails.getUsername());
        TimetableSummaryDto summary = timetableService.getTimetableSummary(userDetails.getUsername());
        return ResponseEntity.ok(summary);
    }
}