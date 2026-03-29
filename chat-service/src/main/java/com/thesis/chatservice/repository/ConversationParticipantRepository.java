package com.thesis.chatservice.repository;

import com.thesis.chatservice.entity.ConversationParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface ConversationParticipantRepository extends JpaRepository<ConversationParticipant, Long> {

    @Query(
        """
            SELECT cp 
            FROM ConversationParticipant cp 
            JOIN FETCH cp.user u           
            WHERE cp.conversation.id = :conversationId 
              AND cp.isActive = true
            """
    )
    List<ConversationParticipant> findActiveParticipantsByConversationId(
        @Param("conversationId") Long conversationId
    );

    @Query(
        "SELECT cp FROM ConversationParticipant cp " +
            "WHERE cp.conversation.id = :conversationId " +
            "AND cp.user.id = :userId " +
            "AND cp.isActive = true"
    )
    Optional<ConversationParticipant> findByConversationIdAndUserId(
        @Param("conversationId") Long conversationId,
        @Param("userId") Long userId
    );

    @Query(
        "SELECT cp FROM ConversationParticipant cp " +
            "WHERE cp.user.id = :userId AND cp.isActive = true"
    )
    List<ConversationParticipant> findActiveParticipantsByUserId(@Param("userId") Long userId);

    @Query(
        "SELECT COUNT(cp) FROM ConversationParticipant cp " +
            "WHERE cp.conversation.id = :conversationId AND cp.isActive = true"
    )
    Long countActiveParticipants(@Param("conversationId") Long conversationId);

    boolean existsByConversationIdAndUserIdAndIsActiveTrue(
        Long conversationId,
        Long userId
    );

}
