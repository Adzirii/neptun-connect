import React, { createContext, useContext, useState, useEffect, ReactNode } from 'react';
import { Message, CreateMessageRequest } from '../types/types';
import apiClient from '../api/apiClient';
import { useConversations } from './ConversationContext';

interface MessageContextType {
    messages: Message[];
    loading: boolean;
    hasMore: boolean;
    error: string | null;
    loadMessages: (conversationId: number, page?: number) => Promise<void>;
    sendMessage: (data: CreateMessageRequest) => Promise<Message>;
    updateMessage: (messageId: number, content: string) => Promise<void>;
    deleteMessage: (messageId: number) => Promise<void>;
    markAsRead: (messageId: number) => Promise<void>;
    markConversationAsRead: (conversationId: number) => Promise<void>;
    searchMessages: (conversationId: number, query: string) => Promise<Message[]>;
    addMessage: (message: Message) => void;
    clearMessages: () => void;
    clearError: () => void;
}

const MessageContext = createContext<MessageContextType | undefined>(undefined);

interface MessageProviderProps {
    children: ReactNode;
}

export const MessageProvider: React.FC<MessageProviderProps> = ({ children }) => {
    const { selectedConversation } = useConversations();
    const [messages, setMessages] = useState<Message[]>([]);
    const [loading, setLoading] = useState<boolean>(false);
    const [hasMore, setHasMore] = useState<boolean>(true);
    const [error, setError] = useState<string | null>(null);
    const [currentPage, setCurrentPage] = useState<number>(0);

    useEffect(() => {
        if (selectedConversation) {
            clearMessages();
            loadMessages(selectedConversation.id, 0);
        } else {
            clearMessages();
        }
    }, [selectedConversation?.id]);

    const loadMessages = async (conversationId: number, page: number = 0) => {
        if (loading) return;

        setLoading(true);
        setError(null);

        try {
            const response = await apiClient.getMessages(conversationId, page, 50);

            const newMessages = response.content.reverse();

            if (page === 0) {
                setMessages(newMessages);
            } else {
                setMessages((prev) => [...newMessages, ...prev]);
            }

            setHasMore(!response.last);
            setCurrentPage(page);
        } catch (err) {
            setError('Failed to load messages');
            console.error('Error loading messages:', err);
        } finally {
            setLoading(false);
        }
    };

    const sendMessage = async (data: CreateMessageRequest): Promise<Message> => {
        setError(null);

        try {
            const newMessage = await apiClient.sendMessage(data);

            setMessages((prev) => [...prev, newMessage]);

            return newMessage;
        } catch (err) {
            setError('Failed to send message');
            throw err;
        }
    };

    const updateMessage = async (messageId: number, content: string) => {
        setError(null);

        try {
            const updatedMessage = await apiClient.updateMessage(messageId, content);

            setMessages((prev) =>
                prev.map((msg) => (msg.id === messageId ? updatedMessage : msg))
            );
        } catch (err) {
            setError('Failed to update message');
            throw err;
        }
    };

    const deleteMessage = async (messageId: number) => {
        setError(null);

        try {
            await apiClient.deleteMessage(messageId);

            setMessages((prev) =>
                prev.map((msg) =>
                    msg.id === messageId
                        ? { ...msg, content: '[Message deleted]', isDeleted: true }
                        : msg
                )
            );
        } catch (err) {
            setError('Failed to delete message');
            throw err;
        }
    };

    const markAsRead = async (messageId: number) => {
        try {
            await apiClient.markMessageAsRead(messageId);

            setMessages((prev) =>
                prev.map((msg) =>
                    msg.id === messageId
                        ? { ...msg, readCount: (msg.readCount || 0) + 1 }
                        : msg
                )
            );
        } catch (err) {
            console.error('Failed to mark message as read:', err);
        }
    };

    const markConversationAsRead = async (conversationId: number) => {
        try {
            await apiClient.markConversationAsRead(conversationId);
        } catch (err) {
            console.error('Failed to mark conversation as read:', err);
        }
    };

    const searchMessages = async (
        conversationId: number,
        query: string
    ): Promise<Message[]> => {
        setError(null);

        try {
            const response = await apiClient.searchMessages(conversationId, query, 0, 50);
            return response.content;
        } catch (err) {
            setError('Failed to search messages');
            throw err;
        }
    };

    const addMessage = (message: Message) => {
        setMessages((prev) => {
            const exists = prev.find((m) => m.id === message.id);
            if (exists) return prev;
            return [...prev, message];
        });
    };

    const clearMessages = () => {
        setMessages([]);
        setCurrentPage(0);
        setHasMore(true);
        setError(null);
    };

    const clearError = () => {
        setError(null);
    };

    const value: MessageContextType = {
        messages,
        loading,
        hasMore,
        error,
        loadMessages,
        sendMessage,
        updateMessage,
        deleteMessage,
        markAsRead,
        markConversationAsRead,
        searchMessages,
        addMessage,
        clearMessages,
        clearError,
    };

    return <MessageContext.Provider value={value}>{children}</MessageContext.Provider>;
};

export const useMessages = (): MessageContextType => {
    const context = useContext(MessageContext);
    if (!context) {
        throw new Error('useMessages must be used within MessageProvider');
    }
    return context;
};
