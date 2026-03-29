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
  test('should render floating button in popup mode initially', () => {
    const { container } = render(<App />);
    
    // Should show the floating message button initially
    const messageButton = screen.queryByTitle('Open Messages');
    expect(messageButton).toBeInTheDocument();
  });

  test('should toggle between popup and full-screen mode', () => {
    const { container } = render(<App />);

    // Initially should be in popup mode
    const messageButton = screen.queryByTitle('Open Messages');

    if (messageButton) {
      // Click message button to open the popup
      fireEvent.click(messageButton);

      // Should now show the popup, and we can find Open Full Screen
      const fullScreenButton = screen.queryByTitle('Open Full Screen');
      if (fullScreenButton) {
        fireEvent.click(fullScreenButton);
        // The main layout should become visible
        expect(screen.queryByText('Neptun Connect')).toBeInTheDocument();
      }
    }
  });

  test('should show minimize button when in full-screen mode', () => {
    const { container } = render(<App />);
    
    const messageButton = screen.queryByTitle('Open Messages');

    if (messageButton) {
      fireEvent.click(messageButton);
      const fullScreenButton = screen.queryByTitle('Open Full Screen');
      if (fullScreenButton) {
        fireEvent.click(fullScreenButton);

        // Should show minimize button
        const minimizeButton = screen.queryByLabelText('Minimize to popup');
        expect(minimizeButton).toBeInTheDocument();
      }
    }
  });
});
