import config from '../config/config';
import {
    User,
    Message,
    Conversation,
    CreateMessageRequest,
    CreateConversationRequest,
    PageResponse,
    LoginRequest,
    AuthResponse,
    ErrorResponse,
} from '../types/types';

class ApiClient {
    private token: string | null = null;
    private refreshing: boolean = false;
    private refreshSubscribers: Array<(token: string) => void> = [];

    constructor() {
        this.token = localStorage.getItem('jwt_token');
    }

    setToken(token: string): void {
        this.token = token;
        localStorage.setItem('jwt_token', token);
    }

    getToken(): string | null {
        if (!this.token) {
            this.token = localStorage.getItem('jwt_token');
        }
        return this.token;
    }

    clearToken(): void {
        this.token = null;
        localStorage.removeItem('jwt_token');
    }

    private getHeaders(): HeadersInit {
        const headers: HeadersInit = {
            'Content-Type': 'application/json',
        };

        const token = this.getToken();
        if (token) {
            headers['Authorization'] = `Bearer ${token}`;
            headers['X-Neptun-Token'] = token; // Also send as Neptun token for course APIs
        }

        return headers;
    }

    private async handleResponse<T>(response: Response): Promise<T> {
        if (!response.ok) {
            const error: ErrorResponse = await response.json().catch(() => ({
                timestamp: new Date().toISOString(),
                status: response.status,
                error: response.statusText,
                message: 'An error occurred',
                path: '',
            }));

            if (response.status === 401) {
                this.clearToken();
                window.dispatchEvent(new Event('unauthorized'));
            }

            throw error;
        }

        const text = await response.text();
        return text ? JSON.parse(text) : null;
    }

    private async request<T>(
        url: string,
        options: RequestInit = {}
    ): Promise<T> {
        try {
            const response = await fetch(url, {
                ...options,
                headers: this.getHeaders(),
            });

            return await this.handleResponse<T>(response);
        } catch (error) {
            if (error instanceof Error) {
                throw error;
            }
            throw error as ErrorResponse;
        }
    }

    // Authentication
    async login(credentials: LoginRequest): Promise<AuthResponse> {
        const response = await fetch(`${config.neptunApiUrl}/auth/login`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(credentials),
        });

        if (!response.ok) {
            throw new Error('Login failed');
        }

        const data: AuthResponse = await response.json();
        this.setToken(data.token);
        return data;
    }

    async getCurrentUser(): Promise<User> {
        return this.request<User>(`${config.apiBaseUrl}/auth/profile`);
    }

    async syncUser(): Promise<User> {
        return this.request<User>(`${config.apiBaseUrl}/auth/sync`, {
            method: 'POST',
        });
    }

    // Users
    async getUsers(): Promise<User[]> {
        return this.request<User[]>(`${config.apiBaseUrl}/users`);
    }

    async getUserById(userId: number): Promise<User> {
        return this.request<User>(`${config.apiBaseUrl}/users/${userId}`);
    }

    async searchUsers(query: string): Promise<User[]> {
        return this.request<User[]>(
            `${config.apiBaseUrl}/users/search?query=${encodeURIComponent(query)}`
        );
    }

    async updateLastSeen(): Promise<void> {
        return this.request<void>(`${config.apiBaseUrl}/users/last-seen`, {
            method: 'POST',
        });
    }

    // Conversations
    async getConversations(): Promise<Conversation[]> {
        return this.request<Conversation[]>(`${config.apiBaseUrl}/conversations`);
    }

    async getConversationById(conversationId: number): Promise<Conversation> {
        return this.request<Conversation>(
            `${config.apiBaseUrl}/conversations/${conversationId}`
        );
    }

    async createConversation(
        data: CreateConversationRequest
    ): Promise<Conversation> {
        return this.request<Conversation>(`${config.apiBaseUrl}/conversations`, {
            method: 'POST',
            body: JSON.stringify(data),
        });
    }

    async addParticipant(
        conversationId: number,
        userId: number
    ): Promise<void> {
        return this.request<void>(
            `${config.apiBaseUrl}/conversations/${conversationId}/participants/${userId}`,
            { method: 'POST' }
        );
    }

    async removeParticipant(
        conversationId: number,
        userId: number
    ): Promise<void> {
        return this.request<void>(
            `${config.apiBaseUrl}/conversations/${conversationId}/participants/${userId}`,
            { method: 'DELETE' }
        );
    }

    async getParticipants(conversationId: number): Promise<User[]> {
        return this.request<User[]>(
            `${config.apiBaseUrl}/conversations/${conversationId}/participants`
        );
    }

    async updateConversation(
        conversationId: number,
        data: { name?: string; description?: string }
    ): Promise<Conversation> {
        return this.request<Conversation>(
            `${config.apiBaseUrl}/conversations/${conversationId}`,
            {
                method: 'PUT',
                body: JSON.stringify(data),
            }
        );
    }

    async deleteConversation(conversationId: number): Promise<void> {
        return this.request<void>(
            `${config.apiBaseUrl}/conversations/${conversationId}`,
            { method: 'DELETE' }
        );
    }

    async createCourseConversation(
        courseCode: string,
        name?: string
    ): Promise<Conversation> {
        return this.request<Conversation>(
            `${config.apiBaseUrl}/conversations/course`,
            {
                method: 'POST',
                body: JSON.stringify({ courseCode, name }),
            }
        );
    }

    // Courses (Neptun API)
    async getEnrolledCourses(): Promise<any[]> {
        return this.request<any[]>(`${config.neptunApiUrl}/courses/enrolled`);
    }

    // File upload
    async uploadFile(file: File): Promise<{
        filename: string;
        originalFilename: string;
        fileUrl: string;
        fileType: string;
        fileSize: string;
    }> {
        const formData = new FormData();
        formData.append('file', file);

        const token = this.getToken();
        const response = await fetch(`${config.apiBaseUrl}/files/upload`, {
            method: 'POST',
            headers: {
                'Authorization': token ? `Bearer ${token}` : '',
            },
            body: formData,
        });

        if (!response.ok) {
            throw new Error('Failed to upload file');
        }

        return await response.json();
    }

    // Messages
    async getMessages(
        conversationId: number,
        page: number = 0,
        size: number = 50
    ): Promise<PageResponse<Message>> {
        return this.request<PageResponse<Message>>(
            `${config.apiBaseUrl}/messages/conversation/${conversationId}?page=${page}&size=${size}`
        );
    }

    async sendMessage(data: CreateMessageRequest): Promise<Message> {
        return this.request<Message>(`${config.apiBaseUrl}/messages`, {
            method: 'POST',
            body: JSON.stringify(data),
        });
    }

    async updateMessage(messageId: number, content: string): Promise<Message> {
        return this.request<Message>(`${config.apiBaseUrl}/messages/${messageId}`, {
            method: 'PUT',
            body: JSON.stringify({ content }),
        });
    }

    async deleteMessage(messageId: number): Promise<void> {
        return this.request<void>(`${config.apiBaseUrl}/messages/${messageId}`, {
            method: 'DELETE',
        });
    }

    async markMessageAsRead(messageId: number): Promise<void> {
        return this.request<void>(
            `${config.apiBaseUrl}/messages/${messageId}/read`,
            { method: 'POST' }
        );
    }

    async markConversationAsRead(
        conversationId: number,
        since?: string
    ): Promise<void> {
        const url = since
            ? `${config.apiBaseUrl}/messages/conversation/${conversationId}/read?since=${since}`
            : `${config.apiBaseUrl}/messages/conversation/${conversationId}/read`;

        return this.request<void>(url, { method: 'POST' });
    }

    async searchMessages(
        conversationId: number,
        query: string,
        page: number = 0,
        size: number = 20
    ): Promise<PageResponse<Message>> {
        return this.request<PageResponse<Message>>(
            `${config.apiBaseUrl}/messages/conversation/${conversationId}/search?query=${encodeURIComponent(
                query
            )}&page=${page}&size=${size}`
        );
    }
}

export const apiClient = new ApiClient();
export default apiClient;