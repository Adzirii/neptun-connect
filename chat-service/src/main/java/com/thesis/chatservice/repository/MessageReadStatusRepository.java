package com.thesis.chatservice.repository;

import com.thesis.chatservice.entity.MessageReadStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MessageReadStatusRepository extends JpaRepository<MessageReadStatus, Long> {

    @Query("SELECT mrs FROM MessageReadStatus mrs " +
        "WHERE mrs.message.id = :messageId")
    List<MessageReadStatus> findByMessageId(@Param("messageId") Long messageId);

    @Query("SELECT mrs FROM MessageReadStatus mrs " +
        "WHERE mrs.message.id = :messageId AND mrs.user.id = :userId")
    Optional<MessageReadStatus> findByMessageIdAndUserId(@Param("messageId") Long messageId,
        @Param("userId") Long userId);

    @Query("SELECT COUNT(mrs) FROM MessageReadStatus mrs " +
        "WHERE mrs.message.id = :messageId")
    Long countReadByMessage(@Param("messageId") Long messageId);

    boolean existsByMessageIdAndUserId(Long messageId, Long userId);
}
