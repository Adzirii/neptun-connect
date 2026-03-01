package com.thesis.chatservice.repository;

import com.thesis.chatservice.entity.MessageAttachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageAttachmentRepository extends JpaRepository<MessageAttachment, Long> {

    @Query("SELECT ma FROM MessageAttachment ma WHERE ma.message.id = :messageId")
    List<MessageAttachment> findByMessageId(@Param("messageId") Long messageId);

    @Query("SELECT ma FROM MessageAttachment ma " +
           "WHERE ma.message.conversation.id = :conversationId " +
           "AND ma.fileType LIKE 'image/%'")
    List<MessageAttachment> findImagesByConversationId(@Param("conversationId") Long conversationId);
}

