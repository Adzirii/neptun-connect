
import React, { useState, useRef } from 'react';
import { Message } from '../../types/types';
import { formatTime } from '../../utils/utils';
import { Send, Check, CheckCheck, MoreVertical, Edit2, Trash2, Copy, Reply, X, Paperclip, File as FileIcon, Image as ImageIcon, Video, Download, ZoomIn } from 'lucide-react';
import { useClickOutside } from '../../hooks/hooks';
import apiClient from '../../api/apiClient';

interface MediaPreviewModalProps {
    fileUrl: string;
    fileName: string;
    fileType: string;
    isOpen: boolean;
    onClose: () => void;
}

const MediaPreviewModal: React.FC<MediaPreviewModalProps> = ({
    fileUrl,
    fileName,
    fileType,
    isOpen,
    onClose,
}) => {
    if (!isOpen) return null;

    const fullUrl = `http://localhost:8080${fileUrl}`;

    const renderMedia = () => {
        if (fileType.startsWith('image/')) {
            return (
                <img
                    src={fullUrl}
                    alt={fileName}
                    className="max-w-full max-h-[80vh] object-contain"
                />
            );
        } else if (fileType.startsWith('video/')) {
            return (
                <video
                    src={fullUrl}
                    controls
                    className="max-w-full max-h-[80vh]"
                >
                    Your browser does not support the video tag.
                </video>
            );
        } else if (fileType === 'application/pdf') {
            return (
                <iframe
                    src={fullUrl}
                    title={fileName}
                    className="w-full h-[80vh]"
                />
            );
        } else {
            return (
                <div className="text-center p-8">
                    <FileIcon size={64} className="mx-auto mb-4 text-gray-400" />
                    <p className="text-gray-700 mb-4">{fileName}</p>
                    <a
                        href={fullUrl}
                        download
                        className="inline-flex items-center gap-2 px-4 py-2 bg-[#3498DB] text-white rounded-lg hover:bg-[#2980B9] transition-colors shadow-sm"
                    >
                        <Download size={20} />
                        Download
                    </a>
                </div>
            );
        }
    };

    return (
        <div className="fixed inset-0 bg-black bg-opacity-90 flex items-center justify-center p-4 z-50">
            <button
                onClick={onClose}
                className="absolute top-4 right-4 p-2 bg-white rounded-full hover:bg-gray-200 transition-colors"
                aria-label="Close"
            >
                <X size={24} />
            </button>
            <div className="flex flex-col items-center gap-4">
                {renderMedia()}
                <div className="text-white text-center">
                    <p className="font-medium">{fileName}</p>
                </div>
            </div>
        </div>
    );
};

interface MessageBubbleProps {
    message: Message;
    currentUserId: number;
    onEdit?: (messageId: number, content: string) => void;
    onDelete?: (messageId: number) => void;
    onReply?: (messageId: number) => void;
}

export const MessageBubble: React.FC<MessageBubbleProps> = ({
                                                                message,
                                                                currentUserId,
                                                                onEdit,
                                                                onDelete,
                                                                onReply,
                                                            }) => {
    const isOwn = message.sender.id === currentUserId;
    const time = formatTime(message.createdAt);
    const [showMenu, setShowMenu] = useState(false);
    const [isEditing, setIsEditing] = useState(false);
    const [editContent, setEditContent] = useState(message.content);
    const [previewFile, setPreviewFile] = useState<{url: string; name: string; type: string} | null>(null);
    const menuRef = useRef<HTMLDivElement | null>(null);

    useClickOutside(menuRef, () => setShowMenu(false));

    const handleEdit = () => {
        if (onEdit && editContent.trim() !== message.content) {
            onEdit(message.id, editContent);
        }
        setIsEditing(false);
        setShowMenu(false);
    };

    const handleDelete = () => {
        if (onDelete && window.confirm('Are you sure you want to delete this message?')) {
            onDelete(message.id);
        }
        setShowMenu(false);
    };

    const handleCopy = async () => {
        try {
            await navigator.clipboard.writeText(message.content);
        } catch (err) {
            console.error('Failed to copy:', err);
        }
        setShowMenu(false);
    };

    const handleReply = () => {
        if (onReply) {
            onReply(message.id);
        }
        setShowMenu(false);
    };

    const handlePreview = (file: {url: string; name: string; type: string}) => {
        setPreviewFile(file);
    };

    const handleClosePreview = () => {
        setPreviewFile(null);
    };

    if (isEditing) {
        return (
            <div className={`flex ${isOwn ? 'justify-end' : 'justify-start'} mb-4`}>
                <div className="max-w-xs lg:max-w-md">
                    <div className="bg-white border border-gray-300 rounded-lg p-2 shadow-sm">
            <textarea
                value={editContent}
                onChange={(e) => setEditContent(e.target.value)}
                className="w-full px-2 py-1 border-none focus:outline-none resize-none"
                rows={3}
                autoFocus
            />
                        <div className="flex gap-2 mt-2">
                            <button
                                onClick={handleEdit}
                                className="px-3 py-1 bg-[#3498DB] text-white text-sm rounded hover:bg-[#2980B9] font-semibold"
                            >
                                Save
                            </button>
                            <button
                                onClick={() => {
                                    setIsEditing(false);
                                    setEditContent(message.content);
                                }}
                                className="px-3 py-1 bg-gray-200 text-gray-700 text-sm rounded hover:bg-gray-300"
                            >
                                Cancel
                            </button>
                        </div>
                    </div>
                </div>
            </div>
        );
    }

    return (
        <div className={`flex ${isOwn ? 'justify-end' : 'justify-start'} mb-4 group`}>
            <div className={`max-w-xs lg:max-w-md ${isOwn ? 'order-2' : 'order-1'}`}>
                {!isOwn && (
                    <div className="text-xs text-gray-600 mb-1 font-medium">
                        {message.sender.name}
                    </div>
                )}
                <div className="relative">
                    {message.parentMessage && (
                        <div className={`mb-2 px-3 py-2 rounded border-l-4 ${
                            isOwn ? 'bg-[#D6EAF8] border-[#3498DB]' : 'bg-gray-50 border-gray-300'
                        }`}>
                            <div className="text-xs font-semibold opacity-75 mb-1">
                                {message.parentMessage.sender.name}
                            </div>
                            <div className="text-xs opacity-75 truncate">
                                {message.parentMessage.content}
                            </div>
                        </div>
                    )}
                    <div
                        className={`px-4 py-2 rounded-lg shadow-sm ${
                            isOwn ? 'bg-[#3498DB] text-white' : 'bg-white text-gray-900 border border-gray-200'
                        }`}
                    >
                        <p className="text-sm break-words whitespace-pre-wrap">{message.content}</p>
                        {message.attachments && message.attachments.length > 0 && (
                            <div className="mt-2 space-y-2">
                                {message.attachments.map((attachment, index) => {
                                    const fullUrl = `http://localhost:8080${attachment.fileUrl}`;
                                    const isImage = attachment.fileType?.startsWith('image/');
                                    const isVideo = attachment.fileType?.startsWith('video/');

                                    if (isImage) {
                                        return (
                                            <div
                                                key={index}
                                                className="relative cursor-pointer group"
                                                onClick={() => handlePreview({
                                                    url: attachment.fileUrl,
                                                    name: attachment.fileName,
                                                    type: attachment.fileType
                                                })}
                                            >
                                                <img
                                                    src={fullUrl}
                                                    alt={attachment.fileName}
                                                    className="max-w-full rounded-lg max-h-64 object-cover"
                                                />
                                                <div className="absolute inset-0 bg-black bg-opacity-0 group-hover:bg-opacity-30 transition-all rounded-lg flex items-center justify-center">
                                                    <ZoomIn size={32} className="text-white opacity-0 group-hover:opacity-100 transition-opacity" />
                                                </div>
                                                <p className="text-xs mt-1 opacity-75">{attachment.fileName}</p>
                                            </div>
                                        );
                                    } else if (isVideo) {
                                        return (
                                            <div
                                                key={index}
                                                className="relative cursor-pointer"
                                                onClick={() => handlePreview({
                                                    url: attachment.fileUrl,
                                                    name: attachment.fileName,
                                                    type: attachment.fileType
                                                })}
                                            >
                                                <video
                                                    src={fullUrl}
                                                    className="max-w-full rounded-lg max-h-64"
                                                    preload="metadata"
                                                />
                                                <div className="absolute inset-0 bg-black bg-opacity-30 flex items-center justify-center rounded-lg">
                                                    <div className="bg-white bg-opacity-90 rounded-full p-3">
                                                        <Video size={24} className="text-gray-700" />
                                                    </div>
                                                </div>
                                                <p className="text-xs mt-1 opacity-75">{attachment.fileName}</p>
                                            </div>
                                        );
                                    } else {
                                        return (
                                            <button
                                                key={index}
                                                onClick={() => handlePreview({
                                                    url: attachment.fileUrl,
                                                    name: attachment.fileName,
                                                    type: attachment.fileType
                                                })}
                                                className={`flex items-center gap-2 p-2 rounded w-full text-left ${
                                                    isOwn ? 'bg-[#2980B9] hover:bg-[#2471A3]' : 'bg-gray-200 hover:bg-gray-300'
                                                }`}
                                            >
                                                <FileIcon size={16} />
                                                <span className="text-xs truncate flex-1">{attachment.fileName}</span>
                                                <Download size={14} className="opacity-50" />
                                            </button>
                                        );
                                    }
                                })}
                            </div>
                        )}
                        <div
                            className={`flex items-center justify-end gap-1 mt-1 text-xs ${
                                isOwn ? 'text-blue-100' : 'text-gray-500'
                            }`}
                        >
                            <span>{time}</span>
                            {isOwn && message.isEdited && <span>(edited)</span>}
                            {isOwn &&
                                (message.readCount && message.readCount > 0 ? (
                                    <CheckCheck size={14} />
                                ) : (
                                    <Check size={14} />
                                ))}
                        </div>
                    </div>
                    {(isOwn || onReply) && (
                        <div className="absolute right-0 top-0 opacity-0 group-hover:opacity-100 transition-opacity">
                            <button
                                onClick={() => setShowMenu(!showMenu)}
                                className="p-1 hover:bg-gray-200 rounded text-gray-600"
                            >
                                <MoreVertical size={16} />
                            </button>
                            {showMenu && (
                                <div
                                    ref={menuRef}
                                    className="absolute right-0 mt-1 bg-white border border-gray-200 rounded-lg shadow-lg py-1 z-10 min-w-[120px]"
                                >
                                    <button
                                        onClick={() => {
                                            setIsEditing(true);
                                            setShowMenu(false);
                                        }}
                                        className="w-full px-4 py-2 text-left text-sm hover:bg-[#E8F4FD] flex items-center gap-2 text-gray-700"
                                    >
                                        <Edit2 size={14} />
                                        Edit
                                    </button>
                                    {onReply && (
                                        <button
                                            onClick={handleReply}
                                            className="w-full px-4 py-2 text-left text-sm hover:bg-[#E8F4FD] flex items-center gap-2 text-gray-700"
                                        >
                                            <Reply size={14} />
                                            Reply
                                        </button>
                                    )}
                                    <button
                                        onClick={handleCopy}
                                        className="w-full px-4 py-2 text-left text-sm hover:bg-[#E8F4FD] flex items-center gap-2 text-gray-700"
                                    >
                                        <Copy size={14} />
                                        Copy
                                    </button>
                                    {isOwn && onDelete && (
                                        <button
                                            onClick={handleDelete}
                                            className="w-full px-4 py-2 text-left text-sm hover:bg-gray-100 text-red-600 flex items-center gap-2"
                                        >
                                            <Trash2 size={14} />
                                            Delete
                                        </button>
                                    )}
                                </div>
                            )}
                        </div>
                    )}
                </div>
            </div>
            {previewFile && (
                <MediaPreviewModal
                    fileUrl={previewFile.url}
                    fileName={previewFile.name}
                    fileType={previewFile.type}
                    isOpen={!!previewFile}
                    onClose={handleClosePreview}
                />
            )}
        </div>
    );
};

interface MessageInputProps {
    onSend: (content: string, parentMessageId?: number, attachments?: any[]) => void;
    disabled?: boolean;
    placeholder?: string;
    replyingTo?: Message | null;
    onCancelReply?: () => void;
}

export const MessageInput: React.FC<MessageInputProps> = ({
                                                              onSend,
                                                              disabled = false,
                                                              placeholder = 'Type a message...',
                                                              replyingTo = null,
                                                              onCancelReply,
                                                          }) => {
    const [messageText, setMessageText] = useState('');
    const [uploadedFiles, setUploadedFiles] = useState<any[]>([]);
    const [uploading, setUploading] = useState(false);
    const fileInputRef = useRef<HTMLInputElement>(null);

    const handleSubmit = () => {
        if ((messageText.trim() || uploadedFiles.length > 0) && !disabled) {
            onSend(messageText, replyingTo?.id, uploadedFiles);
            setMessageText('');
            setUploadedFiles([]);
            if (onCancelReply) {
                onCancelReply();
            }
        }
    };

    const handleKeyPress = (e: React.KeyboardEvent) => {
        if (e.key === 'Enter' && !e.shiftKey) {
            e.preventDefault();
            handleSubmit();
        }
    };

    const handleFileSelect = async (e: React.ChangeEvent<HTMLInputElement>) => {
        const files = e.target.files;
        if (!files || files.length === 0) return;

        setUploading(true);
        try {
            for (let i = 0; i < files.length; i++) {
                const file = files[i];
                const result = await apiClient.uploadFile(file);
                setUploadedFiles(prev => [...prev, result]);
            }
        } catch (error) {
            console.error('Failed to upload file:', error);
            alert('Failed to upload file');
        } finally {
            setUploading(false);
            if (fileInputRef.current) {
                fileInputRef.current.value = '';
            }
        }
    };

    const removeFile = (index: number) => {
        setUploadedFiles(prev => prev.filter((_, i) => i !== index));
    };

    return (
        <div className="p-4 border-t border-gray-200 bg-white shadow-sm">
            {replyingTo && (
                <div className="mb-2 px-3 py-2 bg-[#E8F4FD] border-l-4 border-[#3498DB] rounded flex items-center justify-between">
                    <div className="flex-1 min-w-0">
                        <div className="text-xs font-semibold text-[#2C3E50] mb-1">
                            Replying to {replyingTo.sender.name}
                        </div>
                        <div className="text-xs text-gray-600 truncate">
                            {replyingTo.content}
                        </div>
                    </div>
                    {onCancelReply && (
                        <button
                            onClick={onCancelReply}
                            className="ml-2 p-1 hover:bg-[#D6EAF8] rounded text-[#3498DB]"
                            aria-label="Cancel reply"
                        >
                            <X size={16} />
                        </button>
                    )}
                </div>
            )}
            {uploadedFiles.length > 0 && (
                <div className="mb-2 flex flex-wrap gap-2">
                    {uploadedFiles.map((file, index) => (
                        <div key={index} className="flex items-center gap-2 px-3 py-2 bg-gray-100 rounded-lg">
                            {file.fileType?.startsWith('image/') ? (
                                <ImageIcon size={16} className="text-blue-600" />
                            ) : (
                                <FileIcon size={16} className="text-gray-600" />
                            )}
                            <span className="text-sm text-gray-700 truncate max-w-[200px]">
                                {file.originalFilename}
                            </span>
                            <button
                                onClick={() => removeFile(index)}
                                className="p-0.5 hover:bg-gray-200 rounded"
                            >
                                <X size={14} />
                            </button>
                        </div>
                    ))}
                </div>
            )}
            <div className="flex items-center gap-2">
                <input
                    ref={fileInputRef}
                    type="file"
                    onChange={handleFileSelect}
                    className="hidden"
                    multiple
                />
                <button
                    onClick={() => fileInputRef.current?.click()}
                    disabled={disabled || uploading}
                    className="p-2 text-[#3498DB] hover:bg-[#E8F4FD] rounded-lg transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
                    aria-label="Attach file"
                    title="Attach file"
                >
                    <Paperclip size={20} />
                </button>
                <input
                    type="text"
                    value={messageText}
                    onChange={(e) => setMessageText(e.target.value)}
                    onKeyPress={handleKeyPress}
                    placeholder={placeholder}
                    disabled={disabled}
                    className="flex-1 px-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-[#3498DB] focus:border-[#3498DB] disabled:bg-gray-100"
                />
                <button
                    onClick={handleSubmit}
                    disabled={(!messageText.trim() && uploadedFiles.length === 0) || disabled || uploading}
                    className="p-2 bg-[#3498DB] text-white rounded-lg hover:bg-[#2980B9] transition-colors disabled:bg-gray-400 disabled:cursor-not-allowed shadow-sm"
                    aria-label="Send message"
                    title="Send message"
                >
                    {uploading ? '...' : <Send size={20} />}
                </button>
            </div>
        </div>
    );
};

interface MessageListProps {
    messages: Message[];
    currentUserId: number;
    loading?: boolean;
    onEdit?: (messageId: number, content: string) => void;
    onDelete?: (messageId: number) => void;
    onReply?: (messageId: number) => void;
    emptyMessage?: string;
}

export const MessageList: React.FC<MessageListProps> = ({
                                                            messages,
                                                            currentUserId,
                                                            loading = false,
                                                            onEdit,
                                                            onDelete,
                                                            onReply,
                                                            emptyMessage = 'No messages yet',
                                                        }) => {
    const messagesEndRef = useRef<HTMLDivElement>(null);

    React.useEffect(() => {
        scrollToBottom();
    }, [messages]);

    const scrollToBottom = () => {
        messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
    };

    if (loading) {
        return (
            <div className="flex items-center justify-center h-full">
                <div className="text-gray-500">Loading messages...</div>
            </div>
        );
    }

    if (messages.length === 0) {
        return (
            <div className="flex items-center justify-center h-full text-gray-500">
                <div className="text-center">
                    <p>{emptyMessage}</p>
                    <p className="text-sm mt-2">Send a message to start the conversation</p>
                </div>
            </div>
        );
    }

    return (
        <div className="flex-1 overflow-y-auto p-4">
            {messages.map((message) => (
                <MessageBubble
                    key={message.id}
                    message={message}
                    currentUserId={currentUserId}
                    onEdit={onEdit}
                    onDelete={onDelete}
                    onReply={onReply}
                />
            ))}
            <div ref={messagesEndRef} />
        </div>
    );
};

interface DateSeparatorProps {
    date: string;
}

export const DateSeparator: React.FC<DateSeparatorProps> = ({ date }) => {
    return (
        <div className="flex items-center justify-center my-4">
            <div className="bg-gray-200 text-gray-600 text-xs px-3 py-1 rounded-full">
                {date}
            </div>
        </div>
    );
};



