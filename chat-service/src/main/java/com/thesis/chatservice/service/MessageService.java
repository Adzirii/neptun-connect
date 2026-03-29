package com.thesis.chatservice.service;

import com.thesis.chatservice.dto.message.CreateMessageRequest;
import com.thesis.chatservice.dto.message.MessageDto;
import com.thesis.chatservice.entity.Conversation;
import com.thesis.chatservice.entity.Message;
import com.thesis.chatservice.entity.MessageAttachment;
import com.thesis.chatservice.entity.MessageReadStatus;
import com.thesis.chatservice.entity.User;
import com.thesis.chatservice.exception.ResourceNotFoundException;
import com.thesis.chatservice.repository.MessageAttachmentRepository;
import com.thesis.chatservice.repository.MessageReadStatusRepository;
import com.thesis.chatservice.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageService {

    private final MessageRepository messageRepository;
    private final MessageAttachmentRepository attachmentRepository;
    private final MessageReadStatusRepository readStatusRepository;
    private final ConversationService conversationService;

    @Transactional(readOnly = true)
    public Message getMessageById(Long messageId) {
        return messageRepository.findByIdWithAttachments(messageId)
            .orElseThrow(() -> new ResourceNotFoundException("Message", "id", messageId));
    }

    @Transactional(readOnly = true)
    public Page<Message> getConversationMessages(Long conversationId, Pageable pageable) {
        return messageRepository.findByConversationId(conversationId, pageable);
    }

    @Transactional
    public Message createMessage(CreateMessageRequest request, User sender) {
        Conversation conversation = conversationService.getConversationById(request.getConversationId());

        conversationService.validateUserAccess(conversation.getId(), sender.getId());

        Message.MessageType messageType = request.getMessageType() != null
            ? Message.MessageType.valueOf(request.getMessageType())
            : Message.MessageType.TEXT;

        Message parentMessage = null;
        if (request.getParentMessageId() != null) {
            parentMessage = getMessageById(request.getParentMessageId());
        }

        String messageContent = request.getContent();

        if ((messageContent == null || messageContent.trim().isEmpty() || messageContent.equals("File attached"))
            && request.getAttachments() != null && !request.getAttachments().isEmpty()) {

            List<String> fileNames = new ArrayList<>();
            for (CreateMessageRequest.AttachmentInfo attachInfo : request.getAttachments()) {
                fileNames.add(attachInfo.getFileName());
            }

            if (fileNames.size() == 1) {
                messageContent = "📎 " + fileNames.get(0);
            } else {
                messageContent = "📎 " + String.join(", ", fileNames);
            }
        }

        Message message = Message.builder()
            .conversation(conversation)
            .sender(sender)
            .content(messageContent)
            .messageType(messageType)
            .parentMessage(parentMessage)
            .attachments(new ArrayList<>())
            .build();

        message = messageRepository.save(message);

        if (request.getAttachments() != null && !request.getAttachments().isEmpty()) {
            final Long savedMessageId = message.getId();
            for (CreateMessageRequest.AttachmentInfo attachInfo : request.getAttachments()) {
                MessageAttachment attachment = MessageAttachment.builder()
                    .message(message)
                    .fileName(attachInfo.getFileName())
                    .fileType(attachInfo.getFileType())
                    .fileSize(attachInfo.getFileSize())
                    .fileUrl(attachInfo.getFileUrl())
                    .build();
                message.getAttachments().add(attachment);
                attachmentRepository.save(attachment);
            }

            message = messageRepository.findByIdWithAttachments(savedMessageId)
                .orElseThrow(() -> new ResourceNotFoundException("Message", "id", savedMessageId));
        }

        conversationService.updateLastMessageTime(conversation.getId());

        log.info(
            "Created message {} in conversation {} by user {} with {} attachments",
            message.getId(), conversation.getId(), sender.getId(), message.getAttachments().size()
        );

        return message;
    }


    @Transactional
    public Message updateMessage(Long messageId, String newContent, User user) {
        Message message = getMessageById(messageId);

        if (!message.getSender().getId().equals(user.getId())) {
            throw new RuntimeException("User is not authorized to update this message");
        }

        message.setContent(newContent);
        message.setIsEdited(true);
        message = messageRepository.save(message);

        log.info("Updated message {} by user {}", messageId, user.getId());

        return message;
    }

    @Transactional
    public void deleteMessage(Long messageId, User user) {
        Message message = getMessageById(messageId);

        if (!message.getSender().getId().equals(user.getId())) {
            throw new RuntimeException("User is not authorized to delete this message");
        }

        message.setIsDeleted(true);
        message.setContent("[Message deleted]");
        messageRepository.save(message);

        log.info("Deleted message {} by user {}", messageId, user.getId());
    }

    @Transactional
    public void markMessageAsRead(Long messageId, User user) {
        Message message = getMessageById(messageId);

        if (message.getSender().getId().equals(user.getId())) {
            return;
        }

        if (readStatusRepository.existsByMessageIdAndUserId(messageId, user.getId())) {
            return;
        }

        MessageReadStatus readStatus = MessageReadStatus.builder()
            .message(message)
            .user(user)
            .build();

        readStatusRepository.save(readStatus);

        log.debug("Marked message {} as read by user {}", messageId, user.getId());
    }

    @Transactional
    public void markConversationMessagesAsRead(Long conversationId, User user, LocalDateTime since) {
        List<Message> messages = messageRepository.findNewMessagesSince(conversationId, since);

        for (Message message : messages) {
            if (!message.getSender().getId().equals(user.getId()) &&
                !readStatusRepository.existsByMessageIdAndUserId(message.getId(), user.getId())) {
                markMessageAsRead(message.getId(), user);
            }
        }

        conversationService.updateLastReadTime(conversationId, user.getId());
    }


    @Transactional(readOnly = true)
    public Page<Message> searchMessages(Long conversationId, String query, Pageable pageable) {
        return messageRepository.searchInConversation(conversationId, query, pageable);
    }

    @Transactional(readOnly = true)
    public Long getReadCount(Long messageId) {
        return readStatusRepository.countReadByMessage(messageId);
    }

    @Transactional(readOnly = true)
    public List<Message> getMessageReplies(Long parentMessageId) {
        return messageRepository.findReplies(parentMessageId);
    }
}
