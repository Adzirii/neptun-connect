package com.thesis.chatservice.entity;

import com.fasterxml.jackson.annotation.JsonView;
import com.thesis.chatservice.view.Views;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "messages")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonView(Views.Public.class)
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonView(Views.Public.class)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conversation_id", nullable = false)
    @JsonView(Views.Public.class)
    private Conversation conversation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    @JsonView(Views.Public.class)
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(name = "message_type", length = 50)
    private MessageType messageType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_message_id")
    private Message parentMessage;

    @Column(name = "is_edited")
    private Boolean isEdited;

    @Column(name = "is_deleted")
    private Boolean isDeleted;

    @OneToMany(mappedBy = "message", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonView(Views.Public.class)
    private List<MessageAttachment> attachments = new ArrayList<>();

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (messageType == null) {
            messageType = MessageType.TEXT;
        }
        if (isEdited == null) {
            isEdited = false;
        }
        if (isDeleted == null) {
            isDeleted = false;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum MessageType {
        TEXT,
        FILE,
        IMAGE,
        SYSTEM
    }
}