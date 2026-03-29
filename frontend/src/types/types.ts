export interface User {
    id: number;
    neptunCode: string;
    name: string;
    email: string;
    program?: string;
    faculty?: string;
    semester?: number;
    status: string;
    avatarUrl?: string;
    lastSeenAt?: string;
    online?: boolean;
}

export enum MessageType {
    TEXT = 'TEXT',
    FILE = 'FILE',
    IMAGE = 'IMAGE',
    SYSTEM = 'SYSTEM',
}

export interface MessageAttachment {
    id: number;
    fileName: string;
    fileType: string;
    fileSize: number;
    fileUrl: string;
    thumbnailUrl?: string;
}

export interface ParentMessage {
    id: number;
    sender: User;
    content: string;
}

export interface Message {
    id: number;
    conversationId: number;
    sender: User;
    content: string;
    messageType: string;
    parentMessageId?: number;
    parentMessage?: ParentMessage;
    isEdited: boolean;
    isDeleted?: boolean;
    attachments?: MessageAttachment[];
    readCount?: number;
    createdAt: string;
    updatedAt: string;
}

export interface CreateMessageRequest {
    conversationId: number;
    content: string;
    messageType?: string;
    parentMessageId?: number;
    attachments?: Array<{
        fileName: string;
        fileType: string;
        fileSize: number;
        fileUrl: string;
    }>;
}

export enum ConversationType {
    DIRECT = 'DIRECT',
    GROUP = 'GROUP',
    CHANNEL = 'CHANNEL',
    COURSE = 'COURSE',
}

export interface Conversation {
    id: number;
    name?: string;
    type: string;
    courseCode?: string;
    description?: string;
    avatarUrl?: string;
    createdBy?: User;
    participants?: User[];
    lastMessage?: Message;
    unreadCount?: number;
    createdAt: string;
    lastMessageAt?: string;
}

export interface CreateConversationRequest {
    name?: string;
    type: string;
    courseCode?: string;
    description?: string;
    participantIds: number[];
}

export interface Course {
    id: number;
    courseCode: string;
    name: string;
    credits: number;
    instructor: string;
    semester: string;
    status: string;
}

export interface PageResponse<T> {
    content: T[];
    pageable?: {
        pageNumber: number;
        pageSize: number;
    };
    totalPages?: number;
    totalElements?: number;
    last?: boolean;
    first?: boolean;
}

export interface LoginRequest {
    neptunCode: string;
    password: string;
}

export interface AuthResponse {
    token: string;
    tokenType: string;
    expiresIn: number;
    student: User;
}

export interface ValidationError {
    [field: string]: string;
}

export interface ErrorResponse {
    timestamp: string;
    status: number;
    error: string;
    message: string;
    path: string;
    validationErrors?: ValidationError;
}

export type ApiResponse<T> = {
    success: true;
    data: T;
} | {
    success: false;
    error: ErrorResponse;
};
