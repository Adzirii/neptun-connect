import { Client, IMessage, StompSubscription } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import config from '../config/config';
import { Message } from '../types/types';

export type MessageCallback = (message: Message) => void;
export type TypingCallback = (data: TypingIndicator) => void;
export type ConnectionCallback = () => void;

export interface TypingIndicator {
    conversationId: number;
    userId: number;
    userName: string;
    isTyping: boolean;
}

class WebSocketService {
    private client: Client | null = null;
    private subscriptions: Map<string, StompSubscription> = new Map();
    private connected: boolean = false;
    private token: string | null = null;

    constructor() {
        this.client = null;
    }

    connect(token: string, onConnect?: ConnectionCallback): Promise<void> {
        return new Promise((resolve, reject) => {
            this.token = token;

            const socket = new SockJS(`${config.apiBaseUrl.replace('/api', '')}/ws`);

            this.client = new Client({
                webSocketFactory: () => socket as any,
                connectHeaders: {
                    'Authorization': `Bearer ${token}`
                },
                debug: (str) => {
                    console.log('STOMP: ' + str);
                },
                reconnectDelay: 5000,
                heartbeatIncoming: 4000,
                heartbeatOutgoing: 4000,
            });

            this.client.onConnect = () => {
                console.log('WebSocket connected');
                this.connected = true;
                onConnect?.();
                resolve();
            };

            this.client.onStompError = (frame) => {
                console.error('WebSocket error:', frame);
                reject(new Error(frame.headers['message']));
            };

            this.client.onWebSocketClose = () => {
                console.log('WebSocket closed');
                this.connected = false;
            };

            this.client.activate();
        });
    }

    disconnect(): void {
        if (this.client) {
            this.subscriptions.forEach((subscription) => {
                subscription.unsubscribe();
            });
            this.subscriptions.clear();
            this.client.deactivate();
            this.connected = false;
        }
    }

    subscribeToConversation(
        conversationId: number,
        onMessage: MessageCallback
    ): void {
        if (!this.client || !this.connected) {
            console.error('WebSocket not connected');
            return;
        }

        const destination = `/topic/conversations/${conversationId}`;
        const key = `conversation-${conversationId}`;

        if (this.subscriptions.has(key)) {
            console.log(`Already subscribed to conversation ${conversationId}`);
            return;
        }

        const subscription = this.client.subscribe(
            destination,
            (message: IMessage) => {
                const messageData: Message = JSON.parse(message.body);
                onMessage(messageData);
            }
        );

        this.subscriptions.set(key, subscription);
        console.log(`Subscribed to conversation ${conversationId}`);
    }

    unsubscribeFromConversation(conversationId: number): void {
        const key = `conversation-${conversationId}`;
        const subscription = this.subscriptions.get(key);

        if (subscription) {
            subscription.unsubscribe();
            this.subscriptions.delete(key);
            console.log(`Unsubscribed from conversation ${conversationId}`);
        }
    }

    subscribeToTyping(
        conversationId: number,
        onTyping: TypingCallback
    ): void {
        if (!this.client || !this.connected) {
            console.error('WebSocket not connected');
            return;
        }

        const destination = `/topic/conversations/${conversationId}/typing`;
        const key = `typing-${conversationId}`;

        if (this.subscriptions.has(key)) {
            return;
        }

        const subscription = this.client.subscribe(
            destination,
            (message: IMessage) => {
                const typingData: TypingIndicator = JSON.parse(message.body);
                onTyping(typingData);
            }
        );

        this.subscriptions.set(key, subscription);
    }

    sendMessage(conversationId: number, content: string): void {
        if (!this.client || !this.connected) {
            console.error('WebSocket not connected');
            return;
        }

        this.client.publish({
            destination: `/app/chat/${conversationId}`,
            body: JSON.stringify({
                type: 'MESSAGE',
                conversationId,
                content
            })
        });
    }

    sendTypingIndicator(conversationId: number, isTyping: boolean): void {
        if (!this.client || !this.connected) {
            return;
        }

        this.client.publish({
            destination: `/app/typing/${conversationId}`,
            body: JSON.stringify({
                conversationId,
                isTyping
            })
        });
    }

    isConnected(): boolean {
        return this.connected;
    }
}

export const websocketService = new WebSocketService();
export default websocketService;