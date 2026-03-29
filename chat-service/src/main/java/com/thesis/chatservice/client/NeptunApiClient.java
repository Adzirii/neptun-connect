package com.thesis.chatservice.client;

import com.thesis.chatservice.client.dto.NeptunCourseDto;
import com.thesis.chatservice.client.dto.NeptunCourseStudentDto;
import com.thesis.chatservice.client.dto.NeptunStudentDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class NeptunApiClient {

    private final RestTemplate restTemplate;

    @Value("${neptun.api.base-url}")
    private String baseUrl;

    public NeptunStudentDto getStudentByNeptunCode(String neptunCode, String token) {
        String url = baseUrl + "/api/students/" + neptunCode;

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<NeptunStudentDto> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                NeptunStudentDto.class
            );
            return response.getBody();
        } catch (Exception e) {
            log.error("Failed to fetch student from Neptun API: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Get student data without authentication token (for initial data sync)
     * This method attempts to fetch student data without Bearer token
     */
    public NeptunStudentDto getStudentByNeptunCodeWithoutAuth(String neptunCode) {
        String url = baseUrl + "/api/students/" + neptunCode;

        try {
            ResponseEntity<NeptunStudentDto> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                NeptunStudentDto.class
            );
            return response.getBody();
        } catch (Exception e) {
            log.error("Failed to fetch student from Neptun API without auth: {}", e.getMessage());
            return null;
        }
    }

    public List<NeptunCourseDto> getEnrolledCourses(String token) {
        String url = baseUrl + "/api/courses/enrolled";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<List<NeptunCourseDto>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<List<NeptunCourseDto>>() {}
            );
            return response.getBody();
        } catch (Exception e) {
            log.error("Failed to fetch enrolled courses from Neptun API: {}", e.getMessage());
            return List.of();
        }
    }

    public List<NeptunStudentDto> searchStudents(String query, String token) {
        String url = baseUrl + "/api/students/search?query=" + query;

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<List<NeptunStudentDto>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<List<NeptunStudentDto>>() {}
            );
            return response.getBody();
        } catch (Exception e) {
            log.error("Failed to search students from Neptun API: {}", e.getMessage());
            return List.of();
        }
    }

    public List<NeptunCourseStudentDto> getCourseStudents(String courseCode, String token) {
        String url = baseUrl + "/api/courses/" + courseCode + "/students";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<List<NeptunCourseStudentDto>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<List<NeptunCourseStudentDto>>() {}
            );
            return response.getBody();
        } catch (Exception e) {
            log.error("Failed to fetch course students from Neptun API: {}", e.getMessage());
            return List.of();
        }
    }
}
