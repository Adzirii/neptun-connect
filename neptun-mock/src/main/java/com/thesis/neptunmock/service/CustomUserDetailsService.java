package com.thesis.neptunmock.service;

import com.thesis.neptunmock.model.Student;
import com.thesis.neptunmock.repository.MockDataRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;


@Service
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

    private final MockDataRepository repository;

    @Override
    public UserDetails loadUserByUsername(String neptunCode) throws UsernameNotFoundException {
        log.debug("Loading user by neptun code: {}", neptunCode);

        Student student = repository.findStudentByNeptunCode(neptunCode)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Student not found with neptun code: " + neptunCode
                ));

        return User.builder()
                .username(student.getNeptunCode())
                .password(student.getPassword())
                .authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_STUDENT")))
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(!"ACTIVE".equals(student.getStatus()))
                .build();
    }
}
