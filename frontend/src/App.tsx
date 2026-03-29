
import React from 'react';
import { AuthProvider, useAuth } from './contexts/AuthContext';
import { ConversationProvider, useConversations } from './contexts/ConversationContext';
import { MessageProvider, useMessages } from './contexts/MessageContext';
import { WebSocketProvider } from './contexts/WebSocketContext';
import Login from './components/auth/Login';
import {
    ConversationList,
    ConversationHeader,
    EmptyConversationState,
    NewConversationModal,
    EditConversationModal,
    AddParticipantModal,
    ParticipantsModal,
} from './components/conversation/ConversationComponents';
import {
    MessageList,
    MessageInput,
} from './components/message/MessageComponents';
import { MessengerButton } from './components/chat/MessengerPopup';
import { useModal } from './hooks/hooks';
import { MessageSquare, LogOut, Minimize2 } from 'lucide-react';
import { Message } from './types/types';
import apiClient from './api/apiClient';

const ChatWindow: React.FC = () => {
    const { user } = useAuth();
    const { selectedConversation, updateConversationData, deleteConversation, addParticipant, getParticipants } = useConversations();
    const {
        messages,
        loading,
        sendMessage,
        updateMessage,
        deleteMessage,
    } = useMessages();
    const [replyingTo, setReplyingTo] = React.useState<Message | null>(null);
    const [showEditModal, setShowEditModal] = React.useState(false);
    const [showAddParticipantModal, setShowAddParticipantModal] = React.useState(false);
    const [showParticipantsModal, setShowParticipantsModal] = React.useState(false);
    const [participants, setParticipants] = React.useState<number[]>([]);

    React.useEffect(() => {
        if (selectedConversation) {
            getParticipants(selectedConversation.id).then(ps => {
                setParticipants(ps.map(p => p.id));
            });
        }
    }, [selectedConversation?.id, getParticipants]);

    if (!selectedConversation) {
        return <EmptyConversationState />;
    }

    const handleSendMessage = async (content: string, parentMessageId?: number, attachments?: any[]) => {
        try {
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
                    messageContent = `рџ“Ћ ${formattedAttachments[0].fileName}`;
                } else {
                    messageContent = `рџ“Ћ ${formattedAttachments.map(f => f.fileName).join(', ')}`;
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

    const handleSettingsClick = () => {
        setShowEditModal(true);
    };

    const handleEditConversation = async (name?: string, description?: string) => {
        try {
            await updateConversationData(selectedConversation.id, { name, description });
            setShowEditModal(false);
        } catch (error) {
            console.error('Failed to update conversation:', error);
        }
    };

    const handleDeleteConversation = async () => {
        if (window.confirm('Are you sure you want to delete this conversation?')) {
            try {
                await deleteConversation(selectedConversation.id);
            } catch (error) {
                console.error('Failed to delete conversation:', error);
            }
        }
    };

    const handleAddParticipant = async (userId: number) => {
        try {
            await addParticipant(selectedConversation.id, userId);
            setParticipants([...participants, userId]);
            setShowAddParticipantModal(false);
        } catch (error) {
            console.error('Failed to add participant:', error);
        }
    };

    return (
        <div className="h-full flex flex-col bg-white">
            <ConversationHeader 
                conversation={selectedConversation} 
                onSettingsClick={handleSettingsClick}
                onAddParticipantClick={() => setShowAddParticipantModal(true)}
                onViewParticipants={() => setShowParticipantsModal(true)}
            />
            {showEditModal && (
                <EditConversationModal
                    isOpen={showEditModal}
                    conversation={selectedConversation}
                    onClose={() => setShowEditModal(false)}
                    onSave={handleEditConversation}
                    onDelete={handleDeleteConversation}
                />
            )}
            {showAddParticipantModal && selectedConversation.type === 'GROUP' && (
                <AddParticipantModal
                    isOpen={showAddParticipantModal}
                    conversationId={selectedConversation.id}
                    existingParticipantIds={participants}
                    onClose={() => setShowAddParticipantModal(false)}
                    onAdd={handleAddParticipant}
                />
            )}
            {showParticipantsModal && (
                <ParticipantsModal
                    isOpen={showParticipantsModal}
                    conversationId={selectedConversation.id}
                    onClose={() => setShowParticipantsModal(false)}
                />
            )}
            <MessageList
                messages={messages}
                currentUserId={user?.id || 0}
                loading={loading}
                onEdit={handleEditMessage}
                onDelete={handleDeleteMessage}
                onReply={handleReply}
            />
            <MessageInput 
                onSend={handleSendMessage}
                replyingTo={replyingTo}
                onCancelReply={() => setReplyingTo(null)}
            />
        </div>
    );
};

interface MainLayoutProps {
    onMinimize?: () => void;
}

const MainLayout: React.FC<MainLayoutProps> = ({ onMinimize }) => {
    const { user, logout } = useAuth();
    const { conversations, selectConversation, selectedConversation, createConversation, loadConversations } =
        useConversations();
    const { isOpen, open, close } = useModal();
    const [creatingConversation, setCreatingConversation] = React.useState(false);

    const handleCreateConversation = async (name: string | undefined, participantIds: number[]) => {
        setCreatingConversation(true);
        try {
            const conversationType = participantIds.length === 1 ? 'DIRECT' : 'GROUP';
            await createConversation({
                name,
                type: conversationType,
                participantIds,
            });
            close();
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
            close();
        } catch (error: any) {
            console.error('Failed to create course conversation:', error);
            alert(error.message || 'Failed to create course conversation');
        } finally {
            setCreatingConversation(false);
        }
    };

    return (
        <div className="h-screen flex flex-col bg-gray-50">
            <header className="bg-[#2C3E50] px-6 py-3 flex items-center justify-between flex-shrink-0 shadow-md">
                <div className="flex items-center gap-3">
                    <MessageSquare className="text-white" size={28} />
                    <h1 className="text-xl font-bold text-white">Neptun Connect</h1>
                </div>
                <div className="flex items-center gap-4">
                    <div className="text-right">
                        <p className="font-semibold text-white">{user?.name}</p>
                        <p className="text-sm text-gray-300">{user?.neptunCode}</p>
                    </div>
                    {onMinimize && (
                        <button
                            onClick={onMinimize}
                            className="p-2 hover:bg-[#34495E] rounded-lg transition-colors text-white"
                            aria-label="Minimize to popup"
                            title="Minimize to popup"
                        >
                            <Minimize2 size={20} />
                        </button>
                    )}
                    <button
                        onClick={logout}
                        className="p-2 hover:bg-[#34495E] rounded-lg transition-colors text-white"
                        aria-label="Logout"
                        title="Logout"
                    >
                        <LogOut size={20} />
                    </button>
                </div>
            </header>

            <div className="flex-1 flex overflow-hidden bg-[#F5F7FA]">
                <div className="w-80 flex-shrink-0 bg-white border-r border-gray-200">
                    <ConversationList
                        conversations={conversations}
                        selectedId={selectedConversation?.id || null}
                        onSelect={selectConversation}
                        onNewChat={open}
                    />
                </div>

                <div className="flex-1 bg-[#F5F7FA]">
                    <ChatWindow />
                </div>
            </div>

            <NewConversationModal
                isOpen={isOpen}
                onClose={close}
                onCreate={handleCreateConversation}
                onCreateCourse={handleCreateCourseConversation}
                loading={creatingConversation}
            />
        </div>
    );
};

const WebSocketEnabledContent: React.FC = () => {
    const { addMessage } = useMessages();
    const [showFullMessenger, setShowFullMessenger] = React.useState(false);
    const [showPopup, setShowPopup] = React.useState(true);

    const handleMessageReceived = (message: Message) => {
        console.log('New message received via WebSocket:', message);
        addMessage(message);
    };

    const handleSwitchToPopupMode = () => {
        setShowFullMessenger(false);
        setShowPopup(true);
    };

    const handleSwitchToFullScreen = () => {
        setShowFullMessenger(true);
        setShowPopup(false);
    };

    const handleClosePopup = () => {
        setShowPopup(false);
    };

    const handleOpenPopup = () => {
        setShowPopup(true);
    };

    return (
        <WebSocketProvider onMessageReceived={handleMessageReceived}>
            {showFullMessenger ? (
                <MainLayout onMinimize={handleSwitchToPopupMode} />
            ) : (
                <>
                    <div className="h-screen flex items-center justify-center bg-gray-50"
                         style={{
                             backgroundImage: 'url(/background.png)',
                             backgroundSize: 'cover',
                             backgroundPosition: 'center',
                             backgroundRepeat: 'no-repeat'
                         }}>
                    </div>
                    <MessengerButton
                        isOpen={showPopup}
                        onOpen={handleOpenPopup}
                        onClose={handleClosePopup}
                        onOpenFullScreen={handleSwitchToFullScreen}
                    />
                </>
            )}
        </WebSocketProvider>
    );
};

const AppContent: React.FC = () => {
    const { isAuthenticated, loading } = useAuth();

    if (loading) {
        return (
            <div className="min-h-screen flex items-center justify-center bg-gray-50">
                <div className="text-center">
                    <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600 mx-auto mb-4" />
                    <p className="text-gray-600">Loading...</p>
                </div>
            </div>
        );
    }

    if (!isAuthenticated) {
        return <Login />;
    }

    return (
        <ConversationProvider>
            <MessageProvider>
                <WebSocketEnabledContent />
            </MessageProvider>
        </ConversationProvider>
    );
};

const App: React.FC = () => {
    return (
        <AuthProvider>
            <AppContent />
        </AuthProvider>
    );
};

export default App;
