
import React, { useState, useEffect } from 'react';
import { Conversation, User } from '../../types/types';
import { formatRelativeTime, getInitials, stringToColor, truncateText, generateConversationName } from '../../utils/utils';
import { MessageSquare, Plus, Search, MoreVertical, Edit, Trash2, UserPlus, Settings, ArrowLeft } from 'lucide-react';
import { useAuth } from '../../contexts/AuthContext';
import apiClient from '../../api/apiClient';

interface ConversationListItemProps {
    conversation: Conversation;
    isSelected: boolean;
    onClick: () => void;
}

export const ConversationListItem: React.FC<ConversationListItemProps> = ({
                                                                              conversation,
                                                                              isSelected,
                                                                              onClick,
                                                                          }) => {
    const { user } = useAuth();
    const [participants, setParticipants] = useState<User[]>(conversation.participants || []);
    const [loadingParticipants, setLoadingParticipants] = useState(false);

    useEffect(() => {
        if (!conversation.name && (!conversation.participants || conversation.participants.length === 0) && !loadingParticipants) {
            setLoadingParticipants(true);
            apiClient.getParticipants(conversation.id)
                .then(loadedParticipants => {
                    setParticipants(loadedParticipants);
                })
                .catch(err => {
                    console.error('Failed to load participants:', err);
                })
                .finally(() => {
                    setLoadingParticipants(false);
                });
        } else if (conversation.participants) {
            setParticipants(conversation.participants);
        }
    }, [conversation.id, conversation.name, conversation.participants, loadingParticipants]);

    const displayName = conversation.name || 
        (participants.length > 0 && user ? generateConversationName(participants, user.id) : 'Unnamed Chat');
    const lastMessageContent = conversation.lastMessage?.content || '';
    const lastMessageTime = conversation.lastMessageAt
        ? formatRelativeTime(conversation.lastMessageAt)
        : '';

    return (
        <button
            onClick={onClick}
            className={`w-full p-4 flex items-start gap-3 transition-colors border-b border-gray-100 ${
                isSelected ? 'bg-[#E8F4FD] border-l-4 border-l-[#3498DB]' : 'hover:bg-gray-50'
            }`}
        >
            <div
                className="w-12 h-12 rounded-full flex items-center justify-center text-white font-semibold flex-shrink-0 shadow-sm"
                style={{ backgroundColor: stringToColor(displayName) }}
            >
                {getInitials(displayName)}
            </div>
            <div className="flex-1 min-w-0 text-left">
                <div className="flex items-center justify-between mb-1">
                    <h3 className={`font-semibold truncate ${isSelected ? 'text-[#2C3E50]' : 'text-gray-900'}`}>{displayName}</h3>
                    {lastMessageTime && (
                        <span className="text-xs text-gray-500 ml-2 flex-shrink-0">
              {lastMessageTime}
            </span>
                    )}
                </div>
                {conversation.type === 'COURSE' && conversation.courseCode && (
                    <p className="text-xs text-[#3498DB] font-semibold mb-1">{conversation.courseCode}</p>
                )}
                {lastMessageContent && (
                    <p className="text-sm text-gray-600 truncate">
                        {truncateText(lastMessageContent, 50)}
                    </p>
                )}
                {conversation.unreadCount && conversation.unreadCount > 0 && (
                    <span className="inline-block mt-1 px-2 py-0.5 bg-[#E74C3C] text-white text-xs rounded-full font-semibold">
            {conversation.unreadCount}
          </span>
                )}
            </div>
        </button>
    );
};

interface ConversationListProps {
    conversations: Conversation[];
    selectedId: number | null;
    onSelect: (id: number) => void;
    onNewChat: () => void;
    loading?: boolean;
}

export const ConversationList: React.FC<ConversationListProps> = ({
                                                                      conversations,
                                                                      selectedId,
                                                                      onSelect,
                                                                      onNewChat,
                                                                      loading = false,
                                                                  }) => {
    const { user } = useAuth();
    const [searchQuery, setSearchQuery] = useState('');

    const filteredConversations = conversations.filter((conv) => {
        if (!searchQuery.trim()) {
            return true;
        }

        const query = searchQuery.toLowerCase();

        if (conv.name?.toLowerCase().includes(query)) {
            return true;
        }

        if (!conv.name && conv.participants) {
            return conv.participants.some(p =>
                p.id !== user?.id && p.name.toLowerCase().includes(query)
            );
        }

        if (conv.courseCode?.toLowerCase().includes(query)) {
            return true;
        }

        return false;
    });

    return (
        <div className="h-full flex flex-col bg-white border-r border-gray-200">
            <div className="p-4 border-b border-gray-200 bg-white">
                <div className="flex items-center justify-between mb-4">
                    <h2 className="text-xl font-bold text-[#2C3E50]">Messages</h2>
                    <button
                        onClick={onNewChat}
                        className="p-2 hover:bg-[#E8F4FD] rounded-lg transition-colors text-[#3498DB]"
                        aria-label="New conversation"
                        title="New conversation"
                    >
                        <Plus size={20} />
                    </button>
                </div>
                <div className="relative">
                    <Search
                        className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400"
                        size={18}
                    />
                    <input
                        type="text"
                        value={searchQuery}
                        onChange={(e) => setSearchQuery(e.target.value)}
                        placeholder="Search conversations..."
                        className="w-full pl-10 pr-4 py-2 bg-[#F5F7FA] border border-gray-200 rounded-md focus:outline-none focus:ring-2 focus:ring-[#3498DB] focus:border-[#3498DB]"
                    />
                </div>
            </div>

            <div className="flex-1 overflow-y-auto">
                {loading ? (
                    <div className="flex items-center justify-center py-8">
                        <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-[#3498DB]" />
                    </div>
                ) : filteredConversations.length === 0 ? (
                    <div className="p-8 text-center text-gray-500">
                        <MessageSquare size={48} className="mx-auto mb-4 opacity-50" />
                        <p className="font-semibold text-gray-700">No conversations yet</p>
                        <p className="text-sm mt-2">Start a new chat to get connected</p>
                    </div>
                ) : (
                    filteredConversations.map((conv) => (
                        <ConversationListItem
                            key={conv.id}
                            conversation={conv}
                            isSelected={selectedId === conv.id}
                            onClick={() => onSelect(conv.id)}
                        />
                    ))
                )}
            </div>
        </div>
    );
};

interface ConversationHeaderProps {
    conversation: Conversation;
    onSettingsClick?: () => void;
    onAddParticipantClick?: () => void;
    onViewParticipants?: () => void;
    onBackClick?: () => void;
}

export const ConversationHeader: React.FC<ConversationHeaderProps> = ({
                                                                          conversation,
                                                                          onSettingsClick,
                                                                          onAddParticipantClick,
                                                                          onViewParticipants,
                                                                          onBackClick,
                                                                      }) => {
    const { user } = useAuth();
    const [participants, setParticipants] = useState<User[]>(conversation.participants || []);
    const [loadingParticipants, setLoadingParticipants] = useState(false);

    useEffect(() => {
        if (!conversation.name && (!conversation.participants || conversation.participants.length === 0) && !loadingParticipants) {
            setLoadingParticipants(true);
            apiClient.getParticipants(conversation.id)
                .then(loadedParticipants => {
                    setParticipants(loadedParticipants);
                })
                .catch(err => {
                    console.error('Failed to load participants:', err);
                })
                .finally(() => {
                    setLoadingParticipants(false);
                });
        } else if (conversation.participants) {
            setParticipants(conversation.participants);
        }
    }, [conversation.id, conversation.name, conversation.participants, loadingParticipants]);

    const displayName = conversation.name || 
        (participants.length > 0 && user ? generateConversationName(participants, user.id) : 'Unnamed Chat');

    return (
        <div className="p-4 border-b border-gray-200 flex items-center justify-between bg-white shadow-sm">
            <div className="flex items-center gap-3">
                {onBackClick && (
                    <button
                        onClick={onBackClick}
                        className="p-1 hover:bg-[#E8F4FD] rounded-lg transition-colors text-[#2C3E50]"
                        aria-label="Back"
                    >
                        <ArrowLeft size={20} />
                    </button>
                )}
                <div
                    className="w-10 h-10 rounded-full flex items-center justify-center text-white font-semibold shadow-sm"
                    style={{ backgroundColor: stringToColor(displayName) }}
                >
                    {getInitials(displayName)}
                </div>
                <div>
                    <h2 className="font-bold text-[#2C3E50]">{displayName}</h2>
                    {conversation.type === 'COURSE' && conversation.courseCode && (
                        <p className="text-sm text-[#3498DB] font-semibold">{conversation.courseCode}</p>
                    )}
                    {conversation.participants && conversation.participants.length > 0 && onViewParticipants && (
                        <button
                            onClick={onViewParticipants}
                            className="text-sm text-[#3498DB] hover:text-[#2980B9] hover:underline"
                        >
                            {conversation.participants.length} participants
                        </button>
                    )}
                </div>
            </div>
            <div className="flex items-center gap-2">
                {conversation.type === 'GROUP' && onAddParticipantClick && (
                    <button
                        onClick={onAddParticipantClick}
                        className="p-2 hover:bg-[#E8F4FD] rounded-lg transition-colors text-[#3498DB]"
                        title="Add participant"
                    >
                        <UserPlus size={20} />
                    </button>
                )}
                {onSettingsClick && (
                    <button
                        onClick={onSettingsClick}
                        className="p-2 hover:bg-[#E8F4FD] rounded-lg transition-colors text-[#3498DB]"
                        title="Settings"
                    >
                        <MoreVertical size={20} />
                    </button>
                )}
            </div>
        </div>
    );
};

export const EmptyConversationState: React.FC = () => {
    return (
        <div className="h-full flex items-center justify-center text-gray-500 bg-[#F5F7FA]">
            <div className="text-center">
                <MessageSquare size={64} className="mx-auto mb-4 opacity-30 text-[#3498DB]" />
                <p className="text-lg font-semibold text-gray-700">Select a conversation to start chatting</p>
                <p className="text-sm text-gray-500 mt-2">Choose from your existing messages or start a new one</p>
            </div>
        </div>
    );
};

interface NewConversationModalProps {
    isOpen: boolean;
    onClose: () => void;
    onCreate: (name: string | undefined, participantIds: number[]) => void;
    onCreateCourse?: (courseCode: string, customName?: string) => void;
    loading?: boolean;
}

export const NewConversationModal: React.FC<NewConversationModalProps> = ({
                                                                              isOpen,
                                                                              onClose,
                                                                              onCreate,
                                                                              onCreateCourse,
                                                                              loading = false,
                                                                          }) => {
    const [activeTab, setActiveTab] = useState<'users' | 'courses'>('users');
    const [searchQuery, setSearchQuery] = useState('');
    const [conversationName, setConversationName] = useState('');
    const [searchResults, setSearchResults] = useState<User[]>([]);
    const [selectedUsers, setSelectedUsers] = useState<User[]>([]);
    const [searching, setSearching] = useState(false);

    const [courses, setCourses] = useState<any[]>([]);
    const [selectedCourse, setSelectedCourse] = useState<any | null>(null);
    const [customCourseName, setCustomCourseName] = useState('');
    const [loadingCourses, setLoadingCourses] = useState(false);
    const [courseSearchQuery, setCourseSearchQuery] = useState('');

    useEffect(() => {
        if (isOpen && activeTab === 'courses' && courses.length === 0) {
            loadCourses();
        }
    }, [isOpen, activeTab]);

    const loadCourses = async () => {
        setLoadingCourses(true);
        try {
            const apiClient = (await import('../../api/apiClient')).default;
            const enrolledCourses = await apiClient.getEnrolledCourses();
            setCourses(enrolledCourses);
        } catch (error) {
            console.error('Failed to load courses:', error);
        } finally {
            setLoadingCourses(false);
        }
    };

    if (!isOpen) return null;

    const handleSearch = async () => {
        if (!searchQuery.trim()) return;
        setSearching(true);
        try {
            const apiClient = (await import('../../api/apiClient')).default;
            const results = await apiClient.searchUsers(searchQuery);
            setSearchResults(results);
        } catch (error) {
            console.error('Search failed:', error);
        } finally {
            setSearching(false);
        }
    };

    const handleSearchKeyPress = (e: React.KeyboardEvent) => {
        if (e.key === 'Enter') {
            handleSearch();
        }
    };

    const toggleUser = (user: User) => {
        setSelectedUsers((prev) =>
            prev.find((u) => u.id === user.id)
                ? prev.filter((u) => u.id !== user.id)
                : [...prev, user]
        );
    };

    const handleCreate = () => {
        if (selectedUsers.length > 0) {
            const name = selectedUsers.length > 1 ? (conversationName.trim() || undefined) : undefined;
            onCreate(name, selectedUsers.map((u) => u.id));
            setSearchQuery('');
            setConversationName('');
            setSearchResults([]);
            setSelectedUsers([]);
        }
    };

    const handleCreateCourse = () => {
        if (selectedCourse && onCreateCourse) {
            onCreateCourse(selectedCourse.courseCode, customCourseName.trim() || undefined);
            setSelectedCourse(null);
            setCustomCourseName('');
            setCourseSearchQuery('');
        }
    };

    const filteredCourses = courses.filter(
        (course) =>
            course.courseCode.toLowerCase().includes(courseSearchQuery.toLowerCase()) ||
            course.name.toLowerCase().includes(courseSearchQuery.toLowerCase())
    );

    return (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4 z-50">
            <div className="bg-white rounded-lg shadow-xl max-w-md w-full max-h-[80vh] flex flex-col">
                <div className="p-6 border-b border-gray-200 bg-[#F5F7FA]">
                    <h2 className="text-xl font-bold text-[#2C3E50] mb-4">New Conversation</h2>
                    <div className="flex gap-2">
                        <button
                            onClick={() => setActiveTab('users')}
                            className={`flex-1 px-4 py-2 rounded-lg font-semibold transition-colors ${
                                activeTab === 'users'
                                    ? 'bg-[#3498DB] text-white shadow-sm'
                                    : 'bg-white text-gray-700 hover:bg-gray-100 border border-gray-200'
                            }`}
                        >
                            Direct/Group
                        </button>
                        <button
                            onClick={() => setActiveTab('courses')}
                            className={`flex-1 px-4 py-2 rounded-lg font-semibold transition-colors ${
                                activeTab === 'courses'
                                    ? 'bg-[#3498DB] text-white shadow-sm'
                                    : 'bg-white text-gray-700 hover:bg-gray-100 border border-gray-200'
                            }`}
                        >
                            Course
                        </button>
                    </div>
                </div>

                <div className="p-6 flex-1 overflow-y-auto">
                    {activeTab === 'users' ? (
                        <>
                            <div className="mb-4">
                                <label className="block text-sm font-semibold text-gray-700 mb-2">
                                    Add Participants
                                </label>
                                <div className="flex gap-2">
                                    <input
                                        type="text"
                                        value={searchQuery}
                                        onChange={(e) => setSearchQuery(e.target.value)}
                                        onKeyDown={handleSearchKeyPress}
                                        placeholder="Search users..."
                                        className="flex-1 px-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-[#3498DB] focus:border-[#3498DB]"
                                    />
                                    <button
                                        onClick={handleSearch}
                                        disabled={searching}
                                        className="px-4 py-2 bg-[#3498DB] text-white rounded-md hover:bg-[#2980B9] transition-colors disabled:bg-gray-400 shadow-sm"
                                    >
                                        <Search size={20} />
                                    </button>
                                </div>
                            </div>

                            {selectedUsers.length > 1 && (
                                <div className="mb-4">
                                    <label className="block text-sm font-semibold text-gray-700 mb-2">
                                        Group Name (Optional)
                                    </label>
                                    <input
                                        type="text"
                                        value={conversationName}
                                        onChange={(e) => setConversationName(e.target.value)}
                                        placeholder="Enter group name..."
                                        className="w-full px-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-[#3498DB] focus:border-[#3498DB]"
                                    />
                                </div>
                            )}

                    {selectedUsers.length > 0 && (
                        <div className="mb-4">
                            <p className="text-sm font-semibold text-gray-700 mb-2">Selected:</p>
                            <div className="flex flex-wrap gap-2">
                                {selectedUsers.map((user) => (
                                    <span
                                        key={user.id}
                                        className="inline-flex items-center gap-1 px-3 py-1 bg-[#E8F4FD] text-[#2C3E50] rounded-full text-sm font-medium"
                                    >
                    {user.name}
                                        <button
                                            onClick={() => toggleUser(user)}
                                            className="hover:bg-[#D6EAF8] rounded-full p-0.5"
                                        >
                      Г—
                    </button>
                  </span>
                                ))}
                            </div>
                        </div>
                    )}

                    {searchResults.length > 0 && (
                        <div>
                            <p className="text-sm font-medium text-gray-700 mb-2">
                                Search Results:
                            </p>
                            <div className="space-y-2">
                                {searchResults.map((user) => (
                                    <button
                                        key={user.id}
                                        onClick={() => toggleUser(user)}
                                        className={`w-full p-3 rounded-lg text-left transition-colors border-2 ${
                                            selectedUsers.find((u) => u.id === user.id)
                                                ? 'bg-[#E8F4FD] border-[#3498DB]'
                                                : 'bg-gray-50 hover:bg-gray-100 border-transparent'
                                        }`}
                                    >
                                        <div className="font-semibold text-gray-900">{user.name}</div>
                                        <div className="text-sm text-gray-600">{user.neptunCode}</div>
                                    </button>
                                ))}
                            </div>
                        </div>
                    )}
                        </>
                    ) : (
                        <>
                            <div className="mb-4">
                                <label className="block text-sm font-semibold text-gray-700 mb-2">
                                    Search Courses
                                </label>
                                <input
                                    type="text"
                                    value={courseSearchQuery}
                                    onChange={(e) => setCourseSearchQuery(e.target.value)}
                                    placeholder="Search by code or name..."
                                    className="w-full px-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-[#3498DB] focus:border-[#3498DB]"
                                />
                            </div>

                            {selectedCourse && (
                                <div className="mb-4">
                                    <label className="block text-sm font-semibold text-gray-700 mb-2">
                                        Custom Name (Optional)
                                    </label>
                                    <input
                                        type="text"
                                        value={customCourseName}
                                        onChange={(e) => setCustomCourseName(e.target.value)}
                                        placeholder={`${selectedCourse.name} chat`}
                                        className="w-full px-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-[#3498DB] focus:border-[#3498DB]"
                                    />
                                    <p className="mt-1 text-xs text-gray-500">
                                        Leave empty to use default: "{selectedCourse.name} chat"
                                    </p>
                                </div>
                            )}

                            {loadingCourses ? (
                                <div className="text-center py-8 text-gray-500">Loading courses...</div>
                            ) : filteredCourses.length > 0 ? (
                                <div>
                                    <p className="text-sm font-semibold text-gray-700 mb-2">
                                        Select a course:
                                    </p>
                                    <div className="space-y-2 max-h-96 overflow-y-auto">
                                        {filteredCourses.map((course) => (
                                            <button
                                                key={course.courseCode}
                                                onClick={() => setSelectedCourse(course)}
                                                className={`w-full p-3 rounded-lg text-left transition-colors border-2 ${
                                                    selectedCourse?.courseCode === course.courseCode
                                                        ? 'bg-[#E8F4FD] border-[#3498DB]'
                                                        : 'bg-gray-50 hover:bg-gray-100 border-transparent'
                                                }`}
                                            >
                                                <div className="font-semibold text-gray-900">
                                                    {course.courseCode}
                                                </div>
                                                <div className="text-sm text-gray-600">{course.name}</div>
                                                <div className="text-xs text-gray-500 mt-1">
                                                    {course.instructor} вЂў {course.credits} credits
                                                </div>
                                            </button>
                                        ))}
                                    </div>
                                </div>
                            ) : (
                                <div className="text-center py-8 text-gray-500">
                                    {courseSearchQuery ? 'No courses found' : 'No enrolled courses'}
                                </div>
                            )}
                        </>
                    )}
                </div>

                <div className="p-6 border-t border-gray-200 flex gap-3 bg-[#F5F7FA]">
                    <button
                        onClick={onClose}
                        className="flex-1 px-4 py-2 border border-gray-300 text-gray-700 rounded-md hover:bg-gray-50 transition-colors font-semibold"
                    >
                        Cancel
                    </button>
                    <button
                        onClick={activeTab === 'users' ? handleCreate : handleCreateCourse}
                        disabled={
                            loading ||
                            (activeTab === 'users' ? selectedUsers.length === 0 : !selectedCourse)
                        }
                        className="flex-1 px-4 py-2 bg-[#3498DB] text-white rounded-md hover:bg-[#2980B9] transition-colors disabled:bg-gray-400 disabled:cursor-not-allowed font-semibold shadow-sm"
                    >
                        {loading ? 'Creating...' : 'Create'}
                    </button>
                </div>
            </div>
        </div>
    );
};

interface EditConversationModalProps {
    isOpen: boolean;
    conversation: Conversation;
    onClose: () => void;
    onSave: (name?: string, description?: string) => void;
    onDelete: () => void;
    loading?: boolean;
}

export const EditConversationModal: React.FC<EditConversationModalProps> = ({
    isOpen,
    conversation,
    onClose,
    onSave,
    onDelete,
    loading = false,
}) => {
    const [name, setName] = useState(conversation.name || '');
    const [description, setDescription] = useState(conversation.description || '');

    useEffect(() => {
        if (isOpen) {
            setName(conversation.name || '');
            setDescription(conversation.description || '');
        }
    }, [isOpen, conversation]);

    if (!isOpen) return null;

    const handleSave = () => {
        onSave(name.trim() || undefined, description.trim() || undefined);
    };

    return (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4 z-50">
            <div className="bg-white rounded-lg shadow-xl max-w-md w-full">
                <div className="p-6 border-b border-gray-200">
                    <h2 className="text-xl font-bold text-gray-900">Edit Conversation</h2>
                </div>

                <div className="p-6">
                    <div className="mb-4">
                        <label className="block text-sm font-medium text-gray-700 mb-2">
                            Name
                        </label>
                        <input
                            type="text"
                            value={name}
                            onChange={(e) => setName(e.target.value)}
                            placeholder="Conversation name"
                            className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                        />
                    </div>

                    <div className="mb-4">
                        <label className="block text-sm font-medium text-gray-700 mb-2">
                            Description
                        </label>
                        <textarea
                            value={description}
                            onChange={(e) => setDescription(e.target.value)}
                            placeholder="Conversation description"
                            rows={3}
                            className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                        />
                    </div>
                </div>

                <div className="p-6 border-t border-gray-200 flex gap-3">
                    <button
                        onClick={onDelete}
                        className="px-4 py-2 bg-red-600 text-white rounded-lg hover:bg-red-700 transition-colors"
                    >
                        <Trash2 size={16} className="inline mr-2" />
                        Delete
                    </button>
                    <div className="flex-1" />
                    <button
                        onClick={onClose}
                        className="px-4 py-2 border border-gray-300 text-gray-700 rounded-lg hover:bg-gray-50 transition-colors"
                    >
                        Cancel
                    </button>
                    <button
                        onClick={handleSave}
                        disabled={loading}
                        className="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors disabled:bg-gray-400"
                    >
                        {loading ? 'Saving...' : 'Save'}
                    </button>
                </div>
            </div>
        </div>
    );
};

interface NewCourseConversationModalProps {
    isOpen: boolean;
    onClose: () => void;
    onCreate: (courseCode: string, customName?: string) => void;
    loading?: boolean;
}

export const NewCourseConversationModal: React.FC<NewCourseConversationModalProps> = ({
    isOpen,
    onClose,
    onCreate,
    loading = false,
}) => {
    const [courses, setCourses] = useState<any[]>([]);
    const [selectedCourse, setSelectedCourse] = useState<any | null>(null);
    const [customName, setCustomName] = useState('');
    const [loadingCourses, setLoadingCourses] = useState(false);
    const [searchQuery, setSearchQuery] = useState('');

    useEffect(() => {
        if (isOpen) {
            loadCourses();
        }
    }, [isOpen]);

    const loadCourses = async () => {
        setLoadingCourses(true);
        try {
            const apiClient = (await import('../../api/apiClient')).default;
            const enrolledCourses = await apiClient.getEnrolledCourses();
            setCourses(enrolledCourses);
        } catch (error) {
            console.error('Failed to load courses:', error);
        } finally {
            setLoadingCourses(false);
        }
    };

    const handleCreate = () => {
        if (selectedCourse) {
            onCreate(selectedCourse.courseCode, customName.trim() || undefined);
            setSelectedCourse(null);
            setCustomName('');
            setSearchQuery('');
        }
    };

    const filteredCourses = courses.filter(
        (course) =>
            course.courseCode.toLowerCase().includes(searchQuery.toLowerCase()) ||
            course.name.toLowerCase().includes(searchQuery.toLowerCase())
    );

    if (!isOpen) return null;

    return (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4 z-50">
            <div className="bg-white rounded-lg shadow-xl max-w-md w-full max-h-[80vh] flex flex-col">
                <div className="p-6 border-b border-gray-200">
                    <h2 className="text-xl font-bold text-gray-900">Create Course Conversation</h2>
                </div>

                <div className="p-6 flex-1 overflow-y-auto">
                    <div className="mb-4">
                        <label className="block text-sm font-medium text-gray-700 mb-2">
                            Search Courses
                        </label>
                        <input
                            type="text"
                            value={searchQuery}
                            onChange={(e) => setSearchQuery(e.target.value)}
                            placeholder="Search by code or name..."
                            className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                        />
                    </div>

                    {selectedCourse && (
                        <div className="mb-4">
                            <label className="block text-sm font-medium text-gray-700 mb-2">
                                Custom Name (Optional)
                            </label>
                            <input
                                type="text"
                                value={customName}
                                onChange={(e) => setCustomName(e.target.value)}
                                placeholder={`${selectedCourse.name} chat`}
                                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                            />
                            <p className="mt-1 text-xs text-gray-500">
                                Leave empty to use default: "{selectedCourse.name} chat"
                            </p>
                        </div>
                    )}

                    {loadingCourses ? (
                        <div className="text-center py-8 text-gray-500">Loading courses...</div>
                    ) : filteredCourses.length > 0 ? (
                        <div>
                            <p className="text-sm font-medium text-gray-700 mb-2">
                                Select a course:
                            </p>
                            <div className="space-y-2 max-h-80 overflow-y-auto">
                                {filteredCourses.map((course) => (
                                    <button
                                        key={course.courseCode}
                                        onClick={() => setSelectedCourse(course)}
                                        className={`w-full p-3 rounded-lg text-left transition-colors ${
                                            selectedCourse?.courseCode === course.courseCode
                                                ? 'bg-blue-50 border-2 border-blue-500'
                                                : 'bg-gray-50 hover:bg-gray-100 border-2 border-transparent'
                                        }`}
                                    >
                                        <div className="font-medium text-gray-900">
                                            {course.courseCode}
                                        </div>
                                        <div className="text-sm text-gray-600">{course.name}</div>
                                        <div className="text-xs text-gray-500 mt-1">
                                            {course.instructor} вЂў {course.credits} credits
                                        </div>
                                    </button>
                                ))}
                            </div>
                        </div>
                    ) : (
                        <div className="text-center py-8 text-gray-500">
                            {searchQuery ? 'No courses found' : 'No enrolled courses'}
                        </div>
                    )}
                </div>

                <div className="p-6 border-t border-gray-200 flex gap-3">
                    <button
                        onClick={onClose}
                        className="flex-1 px-4 py-2 border border-gray-300 text-gray-700 rounded-lg hover:bg-gray-50 transition-colors"
                    >
                        Cancel
                    </button>
                    <button
                        onClick={handleCreate}
                        disabled={!selectedCourse || loading}
                        className="flex-1 px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors disabled:bg-gray-400 disabled:cursor-not-allowed"
                    >
                        {loading ? 'Creating...' : 'Create'}
                    </button>
                </div>
            </div>
        </div>
    );
};

interface AddParticipantModalProps {
    isOpen: boolean;
    conversationId: number;
    existingParticipantIds: number[];
    onClose: () => void;
    onAdd: (userId: number) => void;
    loading?: boolean;
}

export const AddParticipantModal: React.FC<AddParticipantModalProps> = ({
    isOpen,
    conversationId,
    existingParticipantIds,
    onClose,
    onAdd,
    loading = false,
}) => {
    const [searchQuery, setSearchQuery] = useState('');
    const [searchResults, setSearchResults] = useState<User[]>([]);
    const [searching, setSearching] = useState(false);

    if (!isOpen) return null;

    const handleSearch = async () => {
        if (!searchQuery.trim()) return;
        setSearching(true);
        try {
            const results = await apiClient.searchUsers(searchQuery);
            const filtered = results.filter(u => !existingParticipantIds.includes(u.id));
            setSearchResults(filtered);
        } catch (error) {
            console.error('Search failed:', error);
        } finally {
            setSearching(false);
        }
    };

    const handleSearchKeyPress = (e: React.KeyboardEvent) => {
        if (e.key === 'Enter') {
            handleSearch();
        }
    };

    const handleAdd = (userId: number) => {
        onAdd(userId);
        setSearchQuery('');
        setSearchResults([]);
    };

    return (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4 z-50">
            <div className="bg-white rounded-lg shadow-xl max-w-md w-full max-h-[80vh] flex flex-col">
                <div className="p-6 border-b border-gray-200">
                    <h2 className="text-xl font-bold text-gray-900">Add Participant</h2>
                </div>

                <div className="p-6 flex-1 overflow-y-auto">
                    <div className="mb-4">
                        <label className="block text-sm font-medium text-gray-700 mb-2">
                            Search Users
                        </label>
                        <div className="flex gap-2">
                            <input
                                type="text"
                                value={searchQuery}
                                onChange={(e) => setSearchQuery(e.target.value)}
                                onKeyDown={handleSearchKeyPress}
                                placeholder="Search users..."
                                className="flex-1 px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                            />
                            <button
                                onClick={handleSearch}
                                disabled={searching}
                                className="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors disabled:bg-gray-400"
                            >
                                <Search size={20} />
                            </button>
                        </div>
                    </div>

                    {searchResults.length > 0 && (
                        <div>
                            <p className="text-sm font-medium text-gray-700 mb-2">
                                Search Results:
                            </p>
                            <div className="space-y-2">
                                {searchResults.map((user) => (
                                    <button
                                        key={user.id}
                                        onClick={() => handleAdd(user.id)}
                                        className="w-full p-3 rounded-lg text-left bg-gray-50 hover:bg-gray-100 border-2 border-transparent transition-colors"
                                    >
                                        <div className="font-medium text-gray-900">{user.name}</div>
                                        <div className="text-sm text-gray-600">{user.neptunCode}</div>
                                    </button>
                                ))}
                            </div>
                        </div>
                    )}
                </div>

                <div className="p-6 border-t border-gray-200">
                    <button
                        onClick={onClose}
                        className="w-full px-4 py-2 border border-gray-300 text-gray-700 rounded-lg hover:bg-gray-50 transition-colors"
                    >
                        Close
                    </button>
                </div>
            </div>
        </div>
    );
};

interface ParticipantsModalProps {
    isOpen: boolean;
    conversationId: number;
    onClose: () => void;
}

export const ParticipantsModal: React.FC<ParticipantsModalProps> = ({
    isOpen,
    conversationId,
    onClose,
}) => {
    const [participants, setParticipants] = useState<User[]>([]);
    const [loading, setLoading] = useState(false);

    useEffect(() => {
        if (isOpen) {
            loadParticipants();
        }
    }, [isOpen, conversationId]);

    const loadParticipants = async () => {
        setLoading(true);
        try {
            const apiClient = (await import('../../api/apiClient')).default;
            const users = await apiClient.getParticipants(conversationId);
            setParticipants(users);
        } catch (error) {
            console.error('Failed to load participants:', error);
        } finally {
            setLoading(false);
        }
    };

    if (!isOpen) return null;

    return (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4 z-50">
            <div className="bg-white rounded-lg shadow-xl max-w-md w-full max-h-[80vh] flex flex-col">
                <div className="p-6 border-b border-gray-200">
                    <h2 className="text-xl font-bold text-gray-900">Participants</h2>
                </div>

                <div className="p-6 flex-1 overflow-y-auto">
                    {loading ? (
                        <div className="text-center py-8 text-gray-500">Loading participants...</div>
                    ) : participants.length > 0 ? (
                        <div className="space-y-2">
                            {participants.map((participant) => (
                                <div
                                    key={participant.id}
                                    className="p-3 bg-gray-50 rounded-lg"
                                >
                                    <div className="flex items-center gap-3">
                                        <div
                                            className="w-10 h-10 rounded-full flex items-center justify-center text-white font-semibold"
                                            style={{ backgroundColor: stringToColor(participant.name) }}
                                        >
                                            {getInitials(participant.name)}
                                        </div>
                                        <div className="flex-1">
                                            <div className="font-medium text-gray-900">{participant.name}</div>
                                            <div className="text-sm text-gray-600">{participant.neptunCode}</div>
                                            {participant.email && (
                                                <div className="text-xs text-gray-500">{participant.email}</div>
                                            )}
                                        </div>
                                    </div>
                                </div>
                            ))}
                        </div>
                    ) : (
                        <div className="text-center py-8 text-gray-500">No participants found</div>
                    )}
                </div>

                <div className="p-6 border-t border-gray-200">
                    <button
                        onClick={onClose}
                        className="w-full px-4 py-2 border border-gray-300 text-gray-700 rounded-lg hover:bg-gray-50 transition-colors"
                    >
                        Close
                    </button>
                </div>
            </div>
        </div>
    );
};
