import React, { createContext, useContext, useEffect, useState, ReactNode } from 'react';
import websocketService, { TypingIndicator } from '../services/websocket.service';
import { useAuth } from './AuthContext';
import { Message } from '../types/types';
import apiClient from '../api/apiClient';

interface WebSocketContextType {
    connected: boolean;
    subscribeToConversation: (conversationId: number) => void;
    unsubscribeFromConversation: (conversationId: number) => void;
    sendMessage: (conversationId: number, content: string) => void;
    sendTypingIndicator: (conversationId: number, isTyping: boolean) => void;
    typingUsers: Map<number, Set<number>>;
}

const WebSocketContext = createContext<WebSocketContextType | undefined>(undefined);

interface WebSocketProviderProps {
    children: ReactNode;
    onMessageReceived?: (message: Message) => void;
}

export const WebSocketProvider: React.FC<WebSocketProviderProps> = ({
                                                                        children,
                                                                        onMessageReceived
                                                                    }) => {
    const { isAuthenticated } = useAuth();
    const [connected, setConnected] = useState(false);
    const [typingUsers, setTypingUsers] = useState<Map<number, Set<number>>>(new Map());

    useEffect(() => {
        if (isAuthenticated) {
            const token = apiClient.getToken();
            if (token) {
                websocketService.connect(token, () => {
                    setConnected(true);
                }).catch((error) => {
                    console.error('Failed to connect WebSocket:', error);
                    setConnected(false);
                });
            }
        } else {
            websocketService.disconnect();
            setConnected(false);
        }

        return () => {
            websocketService.disconnect();
        };
    }, [isAuthenticated]);

    const subscribeToConversation = (conversationId: number) => {
        if (!connected) return;

        websocketService.subscribeToConversation(conversationId, (message) => {
            onMessageReceived?.(message);
        });

        websocketService.subscribeToTyping(conversationId, (data: TypingIndicator) => {
            setTypingUsers((prev) => {
                const newMap = new Map(prev);
                const conversationTyping = newMap.get(data.conversationId) || new Set();

                if (data.isTyping) {
                    conversationTyping.add(data.userId);
                } else {
                    conversationTyping.delete(data.userId);
                }

                newMap.set(data.conversationId, conversationTyping);
                return newMap;
            });
        });
    };

    const unsubscribeFromConversation = (conversationId: number) => {
        websocketService.unsubscribeFromConversation(conversationId);
    };

    const sendMessage = (conversationId: number, content: string) => {
        websocketService.sendMessage(conversationId, content);
    };

    const sendTypingIndicator = (conversationId: number, isTyping: boolean) => {
        websocketService.sendTypingIndicator(conversationId, isTyping);
    };

    const value: WebSocketContextType = {
        connected,
        subscribeToConversation,
        unsubscribeFromConversation,
        sendMessage,
        sendTypingIndicator,
        typingUsers,
    };

    return (
        <WebSocketContext.Provider value={value}>
            {children}
        </WebSocketContext.Provider>
    );
};

export const useWebSocket = (): WebSocketContextType => {
    const context = useContext(WebSocketContext);
    if (!context) {
        throw new Error('useWebSocket must be used within WebSocketProvider');
    }
    return context;
};