package com.thesis.chatservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Entity
@Table(name = "conversation_participants")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConversationParticipant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "conversation_id",
        nullable = false
    )
    private Conversation conversation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "user_id",
        nullable = false
    )
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(
        name = "role",
        length = 50
    )
    private ParticipantRole role;

    @Column(
        name = "joined_at",
        nullable = false
    )
    private LocalDateTime joinedAt;

    @Column(name = "left_at")
    private LocalDateTime leftAt;

    @Column(name = "is_active")
    private Boolean isActive;

    @Column(name = "last_read_at")
    private LocalDateTime lastReadAt;

    @Column(name = "notification_enabled")
    private Boolean notificationEnabled;

    @PrePersist
    protected void onCreate() {
        if (joinedAt == null) {
            joinedAt = LocalDateTime.now();
        }
        if (isActive == null) {
            isActive = true;
        }
        if (notificationEnabled == null) {
            notificationEnabled = true;
        }
        if (role == null) {
            role = ParticipantRole.MEMBER;
        }
    }

    public enum ParticipantRole {
        OWNER,
        ADMIN,
        MEMBER
    }

}
