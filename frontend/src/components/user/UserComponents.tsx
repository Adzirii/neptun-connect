
import React from 'react';
import { User } from '../../types/types';
import { getInitials, stringToColor, isUserOnline } from '../../utils/utils';

interface UserAvatarProps {
    user: User;
    size?: 'sm' | 'md' | 'lg' | 'xl';
    showOnline?: boolean;
    className?: string;
}

export const UserAvatar: React.FC<UserAvatarProps> = ({
                                                          user,
                                                          size = 'md',
                                                          showOnline = false,
                                                          className = '',
                                                      }) => {
    const sizeClasses = {
        sm: 'w-8 h-8 text-xs',
        md: 'w-10 h-10 text-sm',
        lg: 'w-12 h-12 text-base',
        xl: 'w-16 h-16 text-lg',
    };

    const onlineSizeClasses = {
        sm: 'w-2 h-2',
        md: 'w-2.5 h-2.5',
        lg: 'w-3 h-3',
        xl: 'w-4 h-4',
    };

    const bgColor = stringToColor(user.name);
    const isOnline = isUserOnline(user);

    return (
        <div className={`relative ${className}`}>
            {user.avatarUrl ? (
                <img
                    src={user.avatarUrl}
                    alt={user.name}
                    className={`${sizeClasses[size]} rounded-full object-cover`}
                />
            ) : (
                <div
                    className={`${sizeClasses[size]} rounded-full flex items-center justify-center text-white font-semibold`}
                    style={{ backgroundColor: bgColor }}
                >
                    {getInitials(user.name)}
                </div>
            )}
            {showOnline && isOnline && (
                <span
                    className={`absolute bottom-0 right-0 ${onlineSizeClasses[size]} bg-green-500 border-2 border-white rounded-full`}
                />
            )}
        </div>
    );
};

interface UserListItemProps {
    user: User;
    onClick?: (user: User) => void;
    selected?: boolean;
    showOnline?: boolean;
}

export const UserListItem: React.FC<UserListItemProps> = ({
                                                              user,
                                                              onClick,
                                                              selected = false,
                                                              showOnline = true,
                                                          }) => {
    return (
        <button
            onClick={() => onClick?.(user)}
            className={`w-full p-3 flex items-center gap-3 hover:bg-gray-50 transition-colors ${
                selected ? 'bg-blue-50 border-l-4 border-blue-500' : ''
            }`}
        >
            <UserAvatar user={user} size="md" showOnline={showOnline} />
            <div className="flex-1 min-w-0 text-left">
                <div className="font-medium text-gray-900 truncate">{user.name}</div>
                <div className="text-sm text-gray-600 truncate">{user.neptunCode}</div>
            </div>
        </button>
    );
};

interface UserListProps {
    users: User[];
    onUserClick?: (user: User) => void;
    selectedUserId?: number;
    loading?: boolean;
    emptyMessage?: string;
}

export const UserList: React.FC<UserListProps> = ({
                                                      users,
                                                      onUserClick,
                                                      selectedUserId,
                                                      loading = false,
                                                      emptyMessage = 'No users found',
                                                  }) => {
    if (loading) {
        return (
            <div className="flex items-center justify-center py-8">
                <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600" />
            </div>
        );
    }

    if (users.length === 0) {
        return (
            <div className="text-center py-8 text-gray-500">
                <p>{emptyMessage}</p>
            </div>
        );
    }

    return (
        <div className="divide-y divide-gray-100">
            {users.map((user) => (
                <UserListItem
                    key={user.id}
                    user={user}
                    onClick={onUserClick}
                    selected={user.id === selectedUserId}
                />
            ))}
        </div>
    );
};

interface UserSearchInputProps {
    value: string;
    onChange: (value: string) => void;
    placeholder?: string;
    loading?: boolean;
}

export const UserSearchInput: React.FC<UserSearchInputProps> = ({
                                                                    value,
                                                                    onChange,
                                                                    placeholder = 'Search users...',
                                                                    loading = false,
                                                                }) => {
    return (
        <div className="relative">
            <input
                type="text"
                value={value}
                onChange={(e) => onChange(e.target.value)}
                placeholder={placeholder}
                className="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
            />
            <div className="absolute left-3 top-1/2 transform -translate-y-1/2">
                {loading ? (
                    <div className="animate-spin rounded-full h-5 w-5 border-b-2 border-blue-600" />
                ) : (
                    <svg
                        className="w-5 h-5 text-gray-400"
                        fill="none"
                        stroke="currentColor"
                        viewBox="0 0 24 24"
                    >
                        <path
                            strokeLinecap="round"
                            strokeLinejoin="round"
                            strokeWidth={2}
                            d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z"
                        />
                    </svg>
                )}
            </div>
        </div>
    );
};

interface UserProfileProps {
    user: User;
    onClose?: () => void;
}

export const UserProfile: React.FC<UserProfileProps> = ({ user, onClose }) => {
    return (
        <div className="bg-white rounded-lg shadow-lg p-6 max-w-md mx-auto">
            <div className="flex justify-between items-start mb-6">
                <h2 className="text-xl font-bold text-gray-900">User Profile</h2>
                {onClose && (
                    <button
                        onClick={onClose}
                        className="text-gray-400 hover:text-gray-600 transition-colors"
                    >
                        <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path
                                strokeLinecap="round"
                                strokeLinejoin="round"
                                strokeWidth={2}
                                d="M6 18L18 6M6 6l12 12"
                            />
                        </svg>
                    </button>
                )}
            </div>

            <div className="flex flex-col items-center mb-6">
                <UserAvatar user={user} size="xl" showOnline />
                <h3 className="mt-4 text-lg font-semibold text-gray-900">{user.name}</h3>
                <p className="text-gray-600">{user.neptunCode}</p>
            </div>

            <div className="space-y-3">
                {user.email && (
                    <div>
                        <label className="text-sm font-medium text-gray-500">Email</label>
                        <p className="text-gray-900">{user.email}</p>
                    </div>
                )}
                {user.program && (
                    <div>
                        <label className="text-sm font-medium text-gray-500">Program</label>
                        <p className="text-gray-900">{user.program}</p>
                    </div>
                )}
                {user.faculty && (
                    <div>
                        <label className="text-sm font-medium text-gray-500">Faculty</label>
                        <p className="text-gray-900">{user.faculty}</p>
                    </div>
                )}
                {user.semester && (
                    <div>
                        <label className="text-sm font-medium text-gray-500">Semester</label>
                        <p className="text-gray-900">{user.semester}</p>
                    </div>
                )}
                <div>
                    <label className="text-sm font-medium text-gray-500">Status</label>
                    <p className="text-gray-900">{user.status}</p>
                </div>
            </div>
        </div>
    );
};
