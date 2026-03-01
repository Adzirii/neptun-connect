package com.thesis.chatservice.service;

import com.thesis.chatservice.client.NeptunApiClient;
import com.thesis.chatservice.client.dto.NeptunCourseDto;
import com.thesis.chatservice.client.dto.NeptunCourseStudentDto;
import com.thesis.chatservice.dto.conversation.CreateConversationRequest;
import com.thesis.chatservice.entity.Conversation;
import com.thesis.chatservice.entity.ConversationParticipant;
import com.thesis.chatservice.entity.User;
import com.thesis.chatservice.exception.BadRequestException;
import com.thesis.chatservice.exception.ResourceNotFoundException;
import com.thesis.chatservice.exception.UnauthorizedException;
import com.thesis.chatservice.repository.ConversationParticipantRepository;
import com.thesis.chatservice.repository.ConversationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConversationService {

    private final ConversationRepository conversationRepository;
    private final ConversationParticipantRepository participantRepository;
    private final UserService userService;
    private final NeptunApiClient neptunApiClient;

    @Transactional(readOnly = true)
    public Conversation getConversationById(Long conversationId) {
        return conversationRepository.findByIdWithCreator(conversationId)
            .orElseThrow(() -> new ResourceNotFoundException("Conversation", "id", conversationId));
    }

    @Transactional(readOnly = true)
    public List<Conversation> getUserConversations(Long userId) {
        return conversationRepository.findUserConversations(userId);
    }

    @Transactional
    public Conversation createConversation(CreateConversationRequest request, User creator) {
        // For DIRECT chats, check if conversation already exists
        if ("DIRECT".equals(request.getType()) && request.getParticipantIds().size() == 1) {
            Long otherUserId = request.getParticipantIds().get(0);
            Optional<Conversation> existingConversation = conversationRepository
                .findDirectConversationBetweenUsers(creator.getId(), otherUserId);

            if (existingConversation.isPresent()) {
                log.info("Direct conversation already exists between users {} and {}",
                    creator.getId(), otherUserId);
                return existingConversation.get();
            }
        }

        // Determine conversation name
        String conversationName = request.getName();

        // For DIRECT chats, don't set a name - let frontend display other user's name
        // For GROUP chats, require a name if more than 1 participant
        if (conversationName == null || conversationName.trim().isEmpty()) {
            conversationName = null;
        }

        Conversation conversation = Conversation.builder()
            .name(conversationName)
            .type(Conversation.ConversationType.valueOf(request.getType()))
            .courseCode(request.getCourseCode())
            .description(request.getDescription())
            .createdBy(creator)
            .build();

        conversation = conversationRepository.save(conversation);

        addParticipant(conversation.getId(), creator.getId(), ConversationParticipant.ParticipantRole.OWNER);

        for (Long participantId : request.getParticipantIds()) {
            if (!participantId.equals(creator.getId())) {
                addParticipant(conversation.getId(), participantId, ConversationParticipant.ParticipantRole.MEMBER);
            }
        }

        return conversation;
    }

    @Transactional
    public Conversation findOrCreateCourseChannel(String courseCode, User creator) {
        return conversationRepository.findByCourseCode(courseCode)
            .orElseGet(() -> {
                log.info("Creating course channel for: {}", courseCode);
                Conversation conversation = Conversation.builder()
                    .name(courseCode)
                    .type(Conversation.ConversationType.COURSE)
                    .courseCode(courseCode)
                    .description("Course channel for " + courseCode)
                    .createdBy(creator)
                    .build();
                conversation = conversationRepository.save(conversation);
                addParticipant(conversation.getId(), creator.getId(), ConversationParticipant.ParticipantRole.OWNER);
                return conversation;
            });
    }

    @Transactional
    public Conversation createCourseConversation(String courseCode, String customName, User creator, String neptunToken) {
        // Check if course conversation already exists
        Optional<Conversation> existingConversation = conversationRepository.findByCourseCode(courseCode);
        if (existingConversation.isPresent()) {
            log.warn("Course conversation already exists for course: {}", courseCode);
            throw new BadRequestException("Course conversation already exists for this course");
        }

        // Get enrolled courses to find the course name
        List<NeptunCourseDto> enrolledCourses = neptunApiClient.getEnrolledCourses(neptunToken);
        NeptunCourseDto courseDto = enrolledCourses.stream()
            .filter(c -> c.getCourseCode().equals(courseCode))
            .findFirst()
            .orElseThrow(() -> new BadRequestException("You are not enrolled in this course"));

        // Get all students enrolled in this course
        List<NeptunCourseStudentDto> courseStudents = neptunApiClient.getCourseStudents(courseCode, neptunToken);

        if (courseStudents.isEmpty()) {
            throw new BadRequestException("No students found for this course");
        }

        // Determine conversation name
        String conversationName = customName != null && !customName.trim().isEmpty()
            ? customName
            : courseDto.getName() + " chat";

        // Create conversation
        Conversation conversation = Conversation.builder()
            .name(conversationName)
            .type(Conversation.ConversationType.COURSE)
            .courseCode(courseCode)
            .description("Course conversation for " + courseDto.getName())
            .createdBy(creator)
            .build();

        conversation = conversationRepository.save(conversation);
        log.info("Created course conversation for: {} with name: {}", courseCode, conversationName);

        // Add creator as owner
        addParticipant(conversation.getId(), creator.getId(), ConversationParticipant.ParticipantRole.OWNER);

        // Add all course students as members
        int addedCount = 0;
        for (NeptunCourseStudentDto student : courseStudents) {
            try {
                // Find user by neptun code
                User user = userService.getUserByNeptunCode(student.getNeptunCode());
                if (user != null && !user.getId().equals(creator.getId())) {
                    addParticipant(conversation.getId(), user.getId(), ConversationParticipant.ParticipantRole.MEMBER);
                    addedCount++;
                }
            } catch (Exception e) {
                log.warn("Could not add student {} to course conversation: {}", student.getNeptunCode(), e.getMessage());
            }
        }

        log.info("Added {} students to course conversation {}", addedCount, courseCode);
        return conversation;
    }

    @Transactional
    public void addParticipant(Long conversationId, Long userId, ConversationParticipant.ParticipantRole role) {
        Conversation conversation = getConversationById(conversationId);
        User user = userService.getUserById(userId);

        if (participantRepository.existsByConversationIdAndUserIdAndIsActiveTrue(conversationId, userId)) {
            log.warn("User {} is already a participant in conversation {}", userId, conversationId);
            return;
        }

        ConversationParticipant participant = ConversationParticipant.builder()
            .conversation(conversation)
            .user(user)
            .role(role)
            .build();

        participantRepository.save(participant);
        log.info("Added user {} to conversation {} with role {}", userId, conversationId, role);
    }

    @Transactional
    public void removeParticipant(Long conversationId, Long userId) {
        ConversationParticipant participant = participantRepository
            .findByConversationIdAndUserId(conversationId, userId)
            .orElseThrow(() -> new ResourceNotFoundException("Participant not found"));

        participant.setIsActive(false);
        participant.setLeftAt(LocalDateTime.now());
        participantRepository.save(participant);

        log.info("Removed user {} from conversation {}", userId, conversationId);
    }

    @Transactional
    public void updateLastMessageTime(Long conversationId) {
        Conversation conversation = getConversationById(conversationId);
        conversation.setLastMessageAt(LocalDateTime.now());
        conversationRepository.save(conversation);
    }

    @Transactional(readOnly = true)
    public List<ConversationParticipant> getConversationParticipants(Long conversationId) {
        return participantRepository.findActiveParticipantsByConversationId(conversationId);
    }

    @Transactional(readOnly = true)
    public void validateUserAccess(Long conversationId, Long userId) {
        if (!conversationRepository.isUserParticipant(conversationId, userId)) {
            throw new UnauthorizedException("User is not a participant of this conversation");
        }
    }

    @Transactional
    public Conversation updateConversation(Long conversationId, com.thesis.chatservice.dto.conversation.UpdateConversationRequest request, User user) {
        Conversation conversation = getConversationById(conversationId);
        validateUserAccess(conversationId, user.getId());

        if (request.getName() != null) {
            conversation.setName(request.getName());
        }
        if (request.getDescription() != null) {
            conversation.setDescription(request.getDescription());
        }

        Conversation updated = conversationRepository.save(conversation);
        log.info("Updated conversation {} by user {}", conversationId, user.getId());
        return updated;
    }

    @Transactional
    public void deleteConversation(Long conversationId, User user) {
        getConversationById(conversationId); // Validate conversation exists
        validateUserAccess(conversationId, user.getId());

        // Soft delete: mark all participants as inactive
        List<ConversationParticipant> participants = getConversationParticipants(conversationId);
        for (ConversationParticipant participant : participants) {
            participant.setIsActive(false);
            participant.setLeftAt(LocalDateTime.now());
            participantRepository.save(participant);
        }

        log.info("Deleted conversation {} by user {}", conversationId, user.getId());
    }

    @Transactional
    public void updateLastReadTime(Long conversationId, Long userId) {
        ConversationParticipant participant = participantRepository
            .findByConversationIdAndUserId(conversationId, userId)
            .orElseThrow(() -> new ResourceNotFoundException("Participant not found"));

        participant.setLastReadAt(LocalDateTime.now());
        participantRepository.save(participant);
    }
}