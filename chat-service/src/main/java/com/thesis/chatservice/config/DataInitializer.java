package com.thesis.chatservice.config;

import com.thesis.chatservice.client.NeptunApiClient;
import com.thesis.chatservice.client.dto.NeptunStudentDto;
import com.thesis.chatservice.dto.user.LoginResponse;
import com.thesis.chatservice.entity.User;
import com.thesis.chatservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Initializes user data by syncing all students from Neptun on first startup
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements ApplicationRunner {

    private final UserRepository userRepository;
    private final NeptunApiClient neptunApiClient;
    private final RestTemplate restTemplate;

    @Value("${neptun.api.base-url}")
    private String neptunBaseUrl;

    private static final Map<String, String> NEPTUN_CREDENTIALS = new HashMap<>() {{
        put("ABC123", "password");
        put("DEF456", "password");
        put("GHI789", "password");
        put("BLOMOE", "password");
        put("JKL012", "password");
        put("MNO345", "password");
        put("PQR678", "password");
        put("STU901", "password");
        put("VWX234", "password");
        put("YZA567", "password");
        put("BCD890", "password");
        put("EFG123", "password");
    }};

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        log.info("Starting data initialization...");

        long existingUsersCount = userRepository.count();

        if (existingUsersCount >= NEPTUN_CREDENTIALS.size()) {
            log.info("Users already initialized (found {} users), skipping sync", existingUsersCount);
            return;
        }

        log.info("Syncing {} students from Neptun...", NEPTUN_CREDENTIALS.size());

        int successCount = 0;
        int failCount = 0;

        for (Map.Entry<String, String> entry : NEPTUN_CREDENTIALS.entrySet()) {
            String neptunCode = entry.getKey();
            String password = entry.getValue();

            try {
                String token = authenticateAndGetToken(neptunCode, password);

                if (token != null) {
                    syncStudent(neptunCode, token);
                    successCount++;
                    log.info("Successfully synced student: {} ({}/{})",
                        neptunCode, successCount + failCount, NEPTUN_CREDENTIALS.size());
                } else {
                    createMinimalUser(neptunCode);
                    failCount++;
                    log.warn("Failed to authenticate {}, created minimal user", neptunCode);
                }

                Thread.sleep(200);

            } catch (Exception e) {
                failCount++;
                log.error("Failed to sync student: {} - {}", neptunCode, e.getMessage());
                createMinimalUser(neptunCode);
            }
        }

        log.info("Data initialization completed. Success: {}, Failed: {}", successCount, failCount);
    }

    private String authenticateAndGetToken(String neptunCode, String password) {
        try {
            String url = neptunBaseUrl + "/api/auth/login";

            Map<String, String> loginRequest = new HashMap<>();
            loginRequest.put("neptunCode", neptunCode);
            loginRequest.put("password", password);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, String>> entity = new HttpEntity<>(loginRequest, headers);

            ResponseEntity<LoginResponse> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                entity,
                LoginResponse.class
            );

            if (response.getBody() != null) {
                return response.getBody().getToken();
            }

        } catch (Exception e) {
            log.error("Failed to authenticate {}: {}", neptunCode, e.getMessage());
        }
        return null;
    }

    private void syncStudent(String neptunCode, String token) {
        try {
            NeptunStudentDto neptunStudent = neptunApiClient.getStudentByNeptunCode(neptunCode, token);

            if (neptunStudent == null) {
                log.warn("Student not found in Neptun: {}", neptunCode);
                createMinimalUser(neptunCode);
                return;
            }

            User user = userRepository.findByNeptunCode(neptunCode)
                .orElseGet(() -> User.builder()
                    .neptunCode(neptunCode)
                    .build());

            user.setName(neptunStudent.getName());
            user.setEmail(neptunStudent.getEmail());
            user.setProgram(neptunStudent.getProgram());
            user.setFaculty(neptunStudent.getFaculty());
            user.setSemester(neptunStudent.getSemester());
            user.setStatus(neptunStudent.getStatus());

            userRepository.save(user);

        } catch (Exception e) {
            log.warn("Failed to fetch from Neptun for {}, creating minimal user: {}", neptunCode, e.getMessage());
            createMinimalUser(neptunCode);
        }
    }

    private void createMinimalUser(String neptunCode) {
        if (!userRepository.existsByNeptunCode(neptunCode)) {
            User user = User.builder()
                .neptunCode(neptunCode)
                .name(neptunCode)
                .email(neptunCode.toLowerCase() + "@pte.hu")
                .status("ACTIVE")
                .build();
            userRepository.save(user);
            log.info("Created minimal user: {}", neptunCode);
        }
    }
}

