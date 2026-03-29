package com.thesis.chatservice.repository;

import com.thesis.chatservice.entity.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, Long> {

    @Query("SELECT c FROM Conversation c " +
        "WHERE c.type = 'COURSE' AND c.courseCode = :courseCode")
    Optional<Conversation> findByCourseCode(@Param("courseCode") String courseCode);

    @Query("SELECT c FROM Conversation c " +
        "LEFT JOIN FETCH c.createdBy " +
        "WHERE c.id = :id")
    Optional<Conversation> findByIdWithCreator(@Param("id") Long id);

    @Query("SELECT DISTINCT c FROM Conversation c " +
        "LEFT JOIN FETCH c.createdBy " +
        "JOIN ConversationParticipant cp ON c.id = cp.conversation.id " +
        "WHERE cp.user.id = :userId AND cp.isActive = true " +
        "ORDER BY c.lastMessageAt DESC NULLS LAST")
    List<Conversation> findUserConversations(@Param("userId") Long userId);

    @Query("SELECT c FROM Conversation c WHERE c.type = :type")
    List<Conversation> findByType(@Param("type") Conversation.ConversationType type);

    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END " +
        "FROM Conversation c " +
        "JOIN ConversationParticipant cp ON c.id = cp.conversation.id " +
        "WHERE c.id = :conversationId AND cp.user.id = :userId AND cp.isActive = true")
    boolean isUserParticipant(@Param("conversationId") Long conversationId,
        @Param("userId") Long userId);

    @Query("""
        SELECT DISTINCT c FROM Conversation c
        LEFT JOIN FETCH c.createdBy
        JOIN ConversationParticipant cp1 ON c.id = cp1.conversation.id
        JOIN ConversationParticipant cp2 ON c.id = cp2.conversation.id
        WHERE c.type = 'DIRECT'
          AND cp1.user.id = :userId1
          AND cp2.user.id = :userId2
          AND cp1.isActive = true
          AND cp2.isActive = true
        """)
    Optional<Conversation> findDirectConversationBetweenUsers(
        @Param("userId1") Long userId1,
        @Param("userId2") Long userId2
    );
}
