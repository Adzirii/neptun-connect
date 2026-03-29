
import React, { createContext, useContext, useState, useEffect, ReactNode } from 'react';
import { Conversation, CreateConversationRequest, User } from '../types/types';
import apiClient from '../api/apiClient';
import { useAuth } from './AuthContext';

interface ConversationContextType {
    conversations: Conversation[];
    selectedConversation: Conversation | null;
    loading: boolean;
    error: string | null;
    loadConversations: () => Promise<void>;
    selectConversation: (conversationId: number | null) => void;
    createConversation: (data: CreateConversationRequest) => Promise<Conversation>;
    addParticipant: (conversationId: number, userId: number) => Promise<void>;
    removeParticipant: (conversationId: number, userId: number) => Promise<void>;
    getParticipants: (conversationId: number) => Promise<User[]>;
    updateConversation: (conversation: Conversation) => void;
    updateConversationData: (conversationId: number, data: { name?: string; description?: string }) => Promise<Conversation>;
    deleteConversation: (conversationId: number) => Promise<void>;
    clearError: () => void;
}

const ConversationContext = createContext<ConversationContextType | undefined>(undefined);

interface ConversationProviderProps {
    children: ReactNode;
}

export const ConversationProvider: React.FC<ConversationProviderProps> = ({ children }) => {
    const { isAuthenticated } = useAuth();
    const [conversations, setConversations] = useState<Conversation[]>([]);
    const [selectedConversation, setSelectedConversation] = useState<Conversation | null>(null);
    const [loading, setLoading] = useState<boolean>(false);
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        if (isAuthenticated) {
            loadConversations();
        } else {
            setConversations([]);
            setSelectedConversation(null);
        }
    }, [isAuthenticated]);

    const loadConversations = async () => {
        setLoading(true);
        setError(null);

        try {
            const data = await apiClient.getConversations();
            setConversations(data);
        } catch (err) {
            setError('Failed to load conversations');
            console.error('Error loading conversations:', err);
        } finally {
            setLoading(false);
        }
    };

    const selectConversation = (conversationId: number | null) => {
        if (conversationId === null) {
            setSelectedConversation(null);
            return;
        }

        const conversation = conversations.find((c) => c.id === conversationId);
        if (conversation) {
            setSelectedConversation(conversation);
        } else {
            apiClient
                .getConversationById(conversationId)
                .then((conv) => {
                    setSelectedConversation(conv);
                    setConversations((prev) => {
                        const exists = prev.find((c) => c.id === conv.id);
                        return exists ? prev : [...prev, conv];
                    });
                })
                .catch((err) => {
                    console.error('Error loading conversation:', err);
                    setError('Failed to load conversation');
                });
        }
    };

    const createConversation = async (
        data: CreateConversationRequest
    ): Promise<Conversation> => {
        setLoading(true);
        setError(null);

        try {
            const newConversation = await apiClient.createConversation(data);
            setConversations((prev) => [newConversation, ...prev]);
            setSelectedConversation(newConversation);
            return newConversation;
        } catch (err) {
            setError('Failed to create conversation');
            throw err;
        } finally {
            setLoading(false);
        }
    };

    const addParticipant = async (conversationId: number, userId: number) => {
        try {
            await apiClient.addParticipant(conversationId, userId);

            if (selectedConversation?.id === conversationId) {
                const updated = await apiClient.getConversationById(conversationId);
                setSelectedConversation(updated);
                updateConversation(updated);
            }
        } catch (err) {
            setError('Failed to add participant');
            throw err;
        }
    };

    const removeParticipant = async (conversationId: number, userId: number) => {
        try {
            await apiClient.removeParticipant(conversationId, userId);

            if (selectedConversation?.id === conversationId) {
                const updated = await apiClient.getConversationById(conversationId);
                setSelectedConversation(updated);
                updateConversation(updated);
            }
        } catch (err) {
            setError('Failed to remove participant');
            throw err;
        }
    };

    const getParticipants = async (conversationId: number): Promise<User[]> => {
        try {
            return await apiClient.getParticipants(conversationId);
        } catch (err) {
            setError('Failed to load participants');
            throw err;
        }
    };

    const updateConversation = (conversation: Conversation) => {
        setConversations((prev) =>
            prev.map((c) => (c.id === conversation.id ? conversation : c))
        );

        if (selectedConversation?.id === conversation.id) {
            setSelectedConversation(conversation);
        }
    };

    const updateConversationData = async (
        conversationId: number,
        data: { name?: string; description?: string }
    ): Promise<Conversation> => {
        setError(null);
        try {
            const updated = await apiClient.updateConversation(conversationId, data);
            updateConversation(updated);
            return updated;
        } catch (err) {
            setError('Failed to update conversation');
            throw err;
        }
    };

    const deleteConversation = async (conversationId: number): Promise<void> => {
        setError(null);
        try {
            await apiClient.deleteConversation(conversationId);
            setConversations((prev) => prev.filter((c) => c.id !== conversationId));
            if (selectedConversation?.id === conversationId) {
                setSelectedConversation(null);
            }
        } catch (err) {
            setError('Failed to delete conversation');
            throw err;
        }
    };

    const clearError = () => {
        setError(null);
    };

    const value: ConversationContextType = {
        conversations,
        selectedConversation,
        loading,
        error,
        loadConversations,
        selectConversation,
        createConversation,
        addParticipant,
        removeParticipant,
        getParticipants,
        updateConversation,
        updateConversationData,
        deleteConversation,
        clearError,
    };

    return (
        <ConversationContext.Provider value={value}>
            {children}
        </ConversationContext.Provider>
    );
};

export const useConversations = (): ConversationContextType => {
    const context = useContext(ConversationContext);
    if (!context) {
        throw new Error('useConversations must be used within ConversationProvider');
    }
    return context;
};
