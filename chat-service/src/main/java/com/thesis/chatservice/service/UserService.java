package com.thesis.chatservice.service;

import com.thesis.chatservice.client.NeptunApiClient;
import com.thesis.chatservice.client.dto.NeptunStudentDto;
import com.thesis.chatservice.entity.User;
import com.thesis.chatservice.exception.ResourceNotFoundException;
import com.thesis.chatservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final NeptunApiClient neptunApiClient;

    @Transactional(readOnly = true)
    public User getUserById(Long userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
    }

    @Transactional(readOnly = true)
    public User getUserByNeptunCode(String neptunCode) {
        return userRepository.findByNeptunCode(neptunCode)
            .orElseThrow(() -> new ResourceNotFoundException("User", "neptunCode", neptunCode));
    }

    @Transactional
    public User findOrCreateUser(String neptunCode) {
        return userRepository.findByNeptunCode(neptunCode)
            .orElseGet(() -> {
                log.info("User not found locally, creating new user: {}", neptunCode);
                User newUser = User.builder()
                    .neptunCode(neptunCode)
                    .name(neptunCode)
                    .email(neptunCode + "@pte.hu")
                    .status("ACTIVE")
                    .build();
                return userRepository.save(newUser);
            });
    }

    @Transactional
    public User syncUserFromNeptun(
        String neptunCode,
        String token
    ) {
        NeptunStudentDto neptunStudent = neptunApiClient.getStudentByNeptunCode(neptunCode, token);

        if (neptunStudent == null) {
            log.warn("Failed to fetch user from Neptun API, using existing or creating minimal user");
            throw new ResourceNotFoundException("User not found");
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

        return userRepository.save(user);
    }

    @Transactional
    public void updateLastSeen(Long userId) {
        User user = getUserById(userId);
        user.setLastSeenAt(LocalDateTime.now());
        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public List<User> searchUsers(String query) {
        return userRepository.searchUsers(query);
    }

    @Transactional(readOnly = true)
    public List<User> getAllActiveUsers() {
        return userRepository.findAllActiveUsers();
    }

    @Transactional(readOnly = true)
    public User getCurrentUser() {
        return null;
    }

}