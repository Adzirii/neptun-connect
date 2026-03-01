package com.thesis.chatservice.websocket;

import com.thesis.chatservice.entity.User;
import com.thesis.chatservice.repository.UserRepository;
import com.thesis.chatservice.security.JwtUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.lang.reflect.Type;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ChatWebSocketControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    private User testUser;
    private String jwtToken;
    private WebSocketStompClient stompClient;

    @BeforeEach
    public void setup() {
        // Create test user
        testUser = User.builder()
            .neptunCode("TEST01")
            .name("Test User")
            .email("test@test.com")
            .status("ACTIVE")
            .build();
        testUser = userRepository.save(testUser);

        // Generate JWT token
        jwtToken = jwtUtil.generateToken(testUser.getNeptunCode(), testUser.getName());

        // Setup WebSocket client
        stompClient = new WebSocketStompClient(new StandardWebSocketClient());
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());
    }

    @AfterEach
    public void cleanup() {
        if (testUser != null && testUser.getId() != null) {
            userRepository.deleteById(testUser.getId());
        }
    }

    @Test
    public void testWebSocketConnectionWithAuthentication() throws Exception {
        // Arrange
        WebSocketHttpHeaders headers = new WebSocketHttpHeaders();
        headers.add("Authorization", "Bearer " + jwtToken);

        StompHeaders stompHeaders = new StompHeaders();
        stompHeaders.add("Authorization", "Bearer " + jwtToken);

        CompletableFuture<StompSession> sessionFuture = new CompletableFuture<>();
        CompletableFuture<Boolean> connectedFuture = new CompletableFuture<>();

        // Act
        stompClient.connectAsync(
            "ws://localhost:" + port + "/ws",
            headers,
            stompHeaders,
            new StompSessionHandlerAdapter() {
                @Override
                public void afterConnected(
                    StompSession session,
                    StompHeaders connectedHeaders
                ) {
                    sessionFuture.complete(session);
                    connectedFuture.complete(true);
                }

                @Override
                public void handleException(
                    StompSession session,
                    StompCommand command,
                    StompHeaders headers,
                    byte[] payload,
                    Throwable exception
                ) {
                    sessionFuture.completeExceptionally(exception);
                    connectedFuture.completeExceptionally(exception);
                }

                @Override
                public void handleTransportError(
                    StompSession session,
                    Throwable exception
                ) {
                    sessionFuture.completeExceptionally(exception);
                    connectedFuture.completeExceptionally(exception);
                }
            }
        );

        // Assert
        Boolean connected = connectedFuture.get(10, TimeUnit.SECONDS);
        assertTrue(connected);

        StompSession session = sessionFuture.get(10, TimeUnit.SECONDS);
        assertNotNull(session);
        assertTrue(session.isConnected());

        // Cleanup
        session.disconnect();
    }

    @Test
    public void testWebSocketConnectionWithoutAuthentication() throws Exception {
        // Arrange
        CompletableFuture<Throwable> errorFuture = new CompletableFuture<>();

        // Act
        stompClient.connectAsync(
            "ws://localhost:" + port + "/ws",
            new StompSessionHandlerAdapter() {
                @Override
                public void handleTransportError(
                    StompSession session,
                    Throwable exception
                ) {
                    errorFuture.complete(exception);
                }
            }
        );

        // Assert
        Throwable exception = errorFuture.get(10, TimeUnit.SECONDS);
        assertNotNull(exception);
        // Connection should fail without authentication
    }

    @Test
    public void testSubscribeToConversation() throws Exception {
        // Arrange
        Long conversationId = 1L;
        WebSocketHttpHeaders headers = new WebSocketHttpHeaders();
        headers.add("Authorization", "Bearer " + jwtToken);

        StompHeaders stompHeaders = new StompHeaders();
        stompHeaders.add("Authorization", "Bearer " + jwtToken);

        CompletableFuture<StompSession> sessionFuture = new CompletableFuture<>();
        CompletableFuture<Boolean> subscribedFuture = new CompletableFuture<>();

        // Act
        stompClient.connectAsync(
            "ws://localhost:" + port + "/ws",
            headers,
            stompHeaders,
            new StompSessionHandlerAdapter() {
                @Override
                public void afterConnected(
                    StompSession session,
                    StompHeaders connectedHeaders
                ) {
                    sessionFuture.complete(session);
                }
            }
        );

        StompSession session = sessionFuture.get(10, TimeUnit.SECONDS);

        // Subscribe to conversation topic
        StompSession.Subscription subscription = session.subscribe(
            "/topic/conversations/" + conversationId,
            new StompFrameHandler() {
                @Override
                public Type getPayloadType(StompHeaders headers) {
                    return String.class;
                }

                @Override
                public void handleFrame(StompHeaders headers, Object payload) {
                    subscribedFuture.complete(true);
                }
            }
        );

        // Assert
        assertNotNull(subscription);
        assertTrue(session.isConnected());

        // Cleanup
        subscription.unsubscribe();
        session.disconnect();
    }
}