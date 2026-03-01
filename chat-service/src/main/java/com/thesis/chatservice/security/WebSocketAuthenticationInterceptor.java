package com.thesis.chatservice.security;

import com.thesis.chatservice.entity.User;
import com.thesis.chatservice.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketAuthenticationInterceptor implements ChannelInterceptor {

    private final JwtUtil jwtUtil;
    private final UserService userService;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(
            message,
            StompHeaderAccessor.class
        );

        if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
            List<String> authorization = accessor.getNativeHeader("Authorization");

            if (authorization != null && !authorization.isEmpty()) {
                String token = authorization.get(0);

                if (token.startsWith("Bearer ")) {
                    token = token.substring(7);
                }

                try {
                    String neptunCode = jwtUtil.extractNeptunCode(token);

                    if (jwtUtil.validateToken(token, neptunCode)) {
                        User user = userService.findOrCreateUser(neptunCode);

                        UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                user,
                                null,
                                Collections.singletonList(
                                    new SimpleGrantedAuthority("ROLE_USER")
                                )
                            );

                        accessor.setUser(authentication);
                        log.info("WebSocket authenticated user: {}", neptunCode);
                    }
                } catch (Exception e) {
                    log.error("WebSocket authentication failed: {}", e.getMessage());
                }
            }
        }

        return message;
    }
}