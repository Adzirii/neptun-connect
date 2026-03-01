package com.thesis.chatservice.repository;

import com.thesis.chatservice.entity.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    @Query("""
        SELECT m FROM Message m
        JOIN FETCH m.sender
        LEFT JOIN FETCH m.attachments
        LEFT JOIN FETCH m.parentMessage p
        LEFT JOIN FETCH p.sender
        WHERE m.id = :id
        """)
    Optional<Message> findByIdWithAttachments(@Param("id") Long id);

    @Query("""
        SELECT m FROM Message m
        JOIN FETCH m.sender
        LEFT JOIN FETCH m.attachments
        LEFT JOIN FETCH m.parentMessage p
        LEFT JOIN FETCH p.sender
        WHERE m.conversation.id = :conversationId
          AND m.isDeleted = false
        ORDER BY m.createdAt DESC
        """)
    Page<Message> findByConversationId(
        @Param("conversationId") Long conversationId,
        Pageable pageable
    );

    @Query("""
        SELECT m FROM Message m
        JOIN FETCH m.sender
        LEFT JOIN FETCH m.attachments
        WHERE m.conversation.id = :conversationId
          AND m.isDeleted = false
          AND m.createdAt > :since
        ORDER BY m.createdAt ASC
        """)
    List<Message> findNewMessagesSince(
        @Param("conversationId") Long conversationId,
        @Param("since") LocalDateTime since
    );

    @Query("""
        SELECT m FROM Message m
        JOIN FETCH m.sender
        LEFT JOIN FETCH m.attachments
        WHERE m.conversation.id = :conversationId
          AND m.isDeleted = false
          AND LOWER(m.content) LIKE LOWER(CONCAT('%', :query, '%'))
        ORDER BY m.createdAt DESC
        """)
    Page<Message> searchInConversation(
        @Param("conversationId") Long conversationId,
        @Param("query") String query,
        Pageable pageable
    );

    @Query("""
        SELECT COUNT(m) FROM Message m
        WHERE m.conversation.id = :conversationId
          AND m.isDeleted = false
          AND m.createdAt > :lastReadAt
        """)
    Long countUnreadMessages(
        @Param("conversationId") Long conversationId,
        @Param("lastReadAt") LocalDateTime lastReadAt
    );

    @Query("""
        SELECT m FROM Message m
        JOIN FETCH m.sender
        LEFT JOIN FETCH m.parentMessage p
        WHERE m.parentMessage.id = :parentId
          AND m.isDeleted = false
        ORDER BY m.createdAt ASC
        """)
    List<Message> findReplies(@Param("parentId") Long parentId);
}