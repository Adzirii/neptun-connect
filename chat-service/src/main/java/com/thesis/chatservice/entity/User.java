package com.thesis.chatservice.entity;

import com.fasterxml.jackson.annotation.JsonView;
import com.thesis.chatservice.view.Views;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonView(Views.Public.class)

public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonView(Views.Public.class)
    private Long id;

    @Column(name = "neptun_code", nullable = false, unique = true, length = 6)
    @JsonView(Views.Public.class)
    private String neptunCode;

    @Column(name = "name", nullable = false)
    @JsonView(Views.Public.class)
    private String name;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "program")
    private String program;

    @Column(name = "faculty")
    private String faculty;

    @Column(name = "semester")
    private Integer semester;

    @Column(name = "status", length = 50)
    private String status;

    @Column(name = "avatar_url", length = 500)
    private String avatarUrl;

    @Column(name = "last_seen_at")
    private LocalDateTime lastSeenAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (status == null) {
            status = "ACTIVE";
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
