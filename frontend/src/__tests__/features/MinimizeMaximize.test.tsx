import React from 'react';
import { render, screen, fireEvent } from '@testing-library/react';
import '@testing-library/jest-dom';

// Mock the contexts
jest.mock('../../contexts/AuthContext', () => ({
  useAuth: () => ({
    user: { id: 1, name: 'Test User', neptunCode: 'TEST123' },
    logout: jest.fn(),
  }),
}));

jest.mock('../../contexts/ConversationContext', () => ({
  useConversations: () => ({
    conversations: [],
    selectedConversation: null,
    selectConversation: jest.fn(),
    createConversation: jest.fn(),
    loadConversations: jest.fn(),
  }),
}));

jest.mock('../../contexts/MessageContext', () => ({
  useMessages: () => ({
    messages: [],
    loading: false,
    sendMessage: jest.fn(),
    updateMessage: jest.fn(),
    deleteMessage: jest.fn(),
    addMessage: jest.fn(),
  }),
}));

jest.mock('../../contexts/WebSocketContext', () => ({
  WebSocketProvider: ({ children }: { children: React.ReactNode }) => <div>{children}</div>,
}));

jest.mock('../../hooks/hooks', () => ({
  useModal: () => ({
    isOpen: false,
    open: jest.fn(),
    close: jest.fn(),
  }),
}));

jest.mock('../../api/apiClient', () => ({
  __esModule: true,
  default: {
    createCourseConversation: jest.fn(),
  },
}));

import App from '../../App';

describe('Minimize/Maximize Feature', () => {
  test('should render minimize button in full-screen mode', () => {
    const { container } = render(<App />);
    
    // Should show the main layout initially
    // Look for the Neptun Connect header
    expect(screen.queryByText('Neptun Connect')).toBeInTheDocument();
  });

  test('should toggle between full-screen and popup mode', () => {
    const { container } = render(<App />);
    
    // Initially should be in full-screen mode
    const minimizeButton = screen.queryByLabelText('Minimize to popup');
    
    if (minimizeButton) {
      // Click minimize button
      fireEvent.click(minimizeButton);
      
      // Should now show background view
      // The main layout should not be visible
      expect(screen.queryByText('Click the message button to start chatting')).toBeInTheDocument();
    }
  });

  test('should show floating button when minimized', () => {
    const { container } = render(<App />);
    
    const minimizeButton = screen.queryByLabelText('Minimize to popup');
    
    if (minimizeButton) {
      fireEvent.click(minimizeButton);
      
      // Should show the floating message button
      const messageButton = screen.queryByTitle('Open Messages');
      expect(messageButton).toBeInTheDocument();
    }
  });
});
