// src/components/chat/MessengerPopup.tsx

import React, { useState } from 'react';
import { MessageSquare, X, Minimize2, Maximize2, ExternalLink } from 'lucide-react';
import { useAuth } from '../../contexts/AuthContext';
import { useConversations } from '../../contexts/ConversationContext';
import { useMessages } from '../../contexts/MessageContext';
import { ConversationList, ConversationHeader, NewConversationModal } from '../conversation/ConversationComponents';
import { MessageList, MessageInput } from '../message/MessageComponents';
import { Message } from '../../types/types';
import apiClient from '../../api/apiClient';

interface MessengerPopupProps {
    isOpen: boolean;
    onClose: () => void;
    onOpenFullScreen: () => void;
}

export const MessengerPopup: React.FC<MessengerPopupProps> = ({ isOpen, onClose, onOpenFullScreen }) => {
    const { user } = useAuth();
    const { selectedConversation, conversations, selectConversation, createConversation, loadConversations } = useConversations();
    const {
        messages,
        loading,
        sendMessage,
        updateMessage,
        deleteMessage,
    } = useMessages();
    const [replyingTo, setReplyingTo] = React.useState<Message | null>(null);
    const [isMinimized, setIsMinimized] = useState(false);
    const [showConversationList, setShowConversationList] = useState(true);
    const [showNewConversationModal, setShowNewConversationModal] = useState(false);
    const [creatingConversation, setCreatingConversation] = useState(false);

    if (!isOpen) return null;

    const handleCreateConversation = async (name: string | undefined, participantIds: number[]) => {
        setCreatingConversation(true);
        try {
            const conversationType = participantIds.length === 1 ? 'DIRECT' : 'GROUP';
            await createConversation({
                name,
                type: conversationType,
                participantIds,
            });
            setShowNewConversationModal(false);
        } catch (error) {
            console.error('Failed to create conversation:', error);
        } finally {
            setCreatingConversation(false);
        }
    };

    const handleCreateCourseConversation = async (courseCode: string, customName?: string) => {
        setCreatingConversation(true);
        try {
            await apiClient.createCourseConversation(courseCode, customName);
            await loadConversations();
            setShowNewConversationModal(false);
        } catch (error: any) {
            console.error('Failed to create course conversation:', error);
            alert(error.message || 'Failed to create course conversation');
        } finally {
            setCreatingConversation(false);
        }
    };

    const handleSendMessage = async (content: string, parentMessageId?: number, attachments?: any[]) => {
        try {
            if (!selectedConversation) return;

            const messageType = attachments && attachments.length > 0 ? 'FILE' : 'TEXT';
            const formattedAttachments = attachments?.map(file => ({
                fileName: file.originalFilename,
                fileType: file.fileType,
                fileSize: parseInt(file.fileSize),
                fileUrl: file.fileUrl,
            }));

            let messageContent = content;
            if ((!messageContent || messageContent.trim() === '') && formattedAttachments && formattedAttachments.length > 0) {
                if (formattedAttachments.length === 1) {
                    messageContent = `📎 ${formattedAttachments[0].fileName}`;
                } else {
                    messageContent = `📎 ${formattedAttachments.map(f => f.fileName).join(', ')}`;
                }
            }

            await sendMessage({
                conversationId: selectedConversation.id,
                content: messageContent,
                messageType,
                parentMessageId,
                attachments: formattedAttachments,
            });
            setReplyingTo(null);
        } catch (error) {
            console.error('Failed to send message:', error);
        }
    };

    const handleReply = (messageId: number) => {
        const message = messages.find(m => m.id === messageId);
        if (message) {
            setReplyingTo(message);
        }
    };

    const handleEditMessage = async (messageId: number, content: string) => {
        try {
            await updateMessage(messageId, content);
        } catch (error) {
            console.error('Failed to update message:', error);
        }
    };

    const handleDeleteMessage = async (messageId: number) => {
        try {
            await deleteMessage(messageId);
        } catch (error) {
            console.error('Failed to delete message:', error);
        }
    };

    return (
        <>
            {/* Overlay */}
            <div
                className={`fixed inset-0 bg-black bg-opacity-30 z-40 transition-opacity duration-300 ${
                    isOpen ? 'opacity-100' : 'opacity-0 pointer-events-none'
                }`}
                onClick={onClose}
                style={{
                    backgroundImage: 'url(/background.png)',
                    backgroundSize: 'cover',
                    backgroundPosition: 'center',
                    backgroundRepeat: 'no-repeat'
                }}
            />

            {/* Messenger Popup */}
            <div className={`fixed bottom-0 right-6 z-50 bg-white rounded-t-lg shadow-2xl transition-all duration-300 ${
                isMinimized ? 'h-14' : 'h-[600px]'
            } ${
                isOpen ? 'translate-y-0 opacity-100' : 'translate-y-full opacity-0'
            } w-[380px] flex flex-col border border-gray-200`}>
                {/* Header */}
                <div className="bg-[#2C3E50] text-white px-4 py-3 rounded-t-lg flex items-center justify-between shadow-md">
                    <div className="flex items-center gap-2">
                        <div className="bg-[#3498DB] p-1.5 rounded-full">
                            <MessageSquare size={16} />
                        </div>
                        <span className="font-semibold">Messages</span>
                    </div>
                    <div className="flex items-center gap-2">
                        <button
                            onClick={onOpenFullScreen}
                            className="p-1.5 hover:bg-[#34495E] rounded transition-colors"
                            title="Open Full Screen"
                        >
                            <ExternalLink size={16} />
                        </button>
                        <button
                            onClick={() => setIsMinimized(!isMinimized)}
                            className="p-1.5 hover:bg-[#34495E] rounded transition-colors"
                            title={isMinimized ? 'Maximize' : 'Minimize'}
                        >
                            {isMinimized ? <Maximize2 size={16} /> : <Minimize2 size={16} />}
                        </button>
                        <button
                            onClick={onClose}
                            className="p-1.5 hover:bg-[#34495E] rounded transition-colors"
                            title="Close"
                        >
                            <X size={16} />
                        </button>
                    </div>
                </div>

                {/* Content */}
                {!isMinimized && (
                    <div className="flex-1 flex overflow-hidden">
                        {showConversationList && (
                            <div className="w-full border-r border-gray-200">
                                <ConversationList
                                    conversations={conversations}
                                    selectedId={selectedConversation?.id || null}
                                    onSelect={(id) => {
                                        selectConversation(id);
                                        setShowConversationList(false);
                                    }}
                                    onNewChat={() => setShowNewConversationModal(true)}
                                />
                            </div>
                        )}

                        {!showConversationList && selectedConversation && (
                            <div className="flex-1 flex flex-col">
                                <div
                                    className="cursor-pointer"
                                    onClick={() => setShowConversationList(true)}
                                >
                                    <ConversationHeader
                                        conversation={selectedConversation}
                                    />
                                </div>
                                <div className="flex-1 overflow-hidden">
                                    <MessageList
                                        messages={messages}
                                        currentUserId={user?.id || 0}
                                        loading={loading}
                                        onEdit={handleEditMessage}
                                        onDelete={handleDeleteMessage}
                                        onReply={handleReply}
                                    />
                                </div>
                                <MessageInput
                                    onSend={handleSendMessage}
                                    replyingTo={replyingTo}
                                    onCancelReply={() => setReplyingTo(null)}
                                />
                            </div>
                        )}
                    </div>
                )}
            </div>

            {/* New Conversation Modal */}
            <NewConversationModal
                isOpen={showNewConversationModal}
                onClose={() => setShowNewConversationModal(false)}
                onCreate={handleCreateConversation}
                onCreateCourse={handleCreateCourseConversation}
                loading={creatingConversation}
            />
        </>
    );
};

// Floating Button Component
interface MessengerButtonProps {
    isOpen?: boolean;
    onOpen?: () => void;
    onClose?: () => void;
    onOpenFullScreen?: () => void;
}

export const MessengerButton: React.FC<MessengerButtonProps> = ({ 
    isOpen: externalIsOpen,
    onOpen: externalOnOpen,
    onClose: externalOnClose,
    onOpenFullScreen: externalOnOpenFullScreen
}) => {
    const [internalIsOpen, setInternalIsOpen] = useState(false);
    const { conversations } = useConversations();

    // Use external state if provided, otherwise use internal state
    const isOpen = externalIsOpen !== undefined ? externalIsOpen : internalIsOpen;
    
    // Handler for opening/closing popup
    const handleTogglePopup = (open: boolean) => {
        if (externalOnOpen && externalOnClose) {
            // Controlled mode: use external handlers
            open ? externalOnOpen() : externalOnClose();
        } else {
            // Uncontrolled mode: use internal state
            setInternalIsOpen(open);
        }
    };

    // Calculate total unread count
    const unreadCount = conversations.reduce((total, conv) => {
        return total + (conv.unreadCount || 0);
    }, 0);

    // Handler for opening full screen
    // Fallback is provided for backwards compatibility if MessengerButton is used standalone
    // In the current architecture, externalOnOpenFullScreen should always be provided
    const handleOpenFullScreen = externalOnOpenFullScreen || (() => {
        handleTogglePopup(false);
        console.warn('MessengerButton: No onOpenFullScreen handler provided. This is expected only in standalone mode.');
    });

    return (
        <>
            {!isOpen && (
                <button
                    onClick={() => handleTogglePopup(true)}
                    className="fixed bottom-6 right-6 z-50 bg-[#3498DB] text-white rounded-full shadow-lg hover:bg-[#2980B9] transition-all duration-300 hover:scale-110 flex items-center justify-center"
                    style={{
                        width: '56px',
                        height: '56px',
                        minWidth: '56px',
                        minHeight: '56px',
                        maxWidth: '56px',
                        maxHeight: '56px',
                        padding: 0
                    }}
                    title="Open Messages"
                >
                    <MessageSquare size={24} />
                    {unreadCount > 0 && (
                        <span className="absolute -top-1 -right-1 bg-[#E74C3C] text-white text-xs font-bold rounded-full h-6 w-6 flex items-center justify-center shadow-md">
                            {unreadCount > 9 ? '9+' : unreadCount}
                        </span>
                    )}
                </button>
            )}

            <MessengerPopup 
                isOpen={isOpen} 
                onClose={() => handleTogglePopup(false)}
                onOpenFullScreen={handleOpenFullScreen}
            />
        </>
    );
};

