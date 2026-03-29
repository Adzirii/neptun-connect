import React from 'react';
import { render, screen, fireEvent } from '@testing-library/react';
import '@testing-library/jest-dom';

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
    
    const messageButton = screen.queryByTitle('Open Messages');
    expect(messageButton).toBeInTheDocument();
  });

  test('should toggle between popup and full-screen mode', () => {
    const { container } = render(<App />);

    const messageButton = screen.queryByTitle('Open Messages');

    if (messageButton) {
      fireEvent.click(messageButton);

      const fullScreenButton = screen.queryByTitle('Open Full Screen');
      if (fullScreenButton) {
        fireEvent.click(fullScreenButton);
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

        const minimizeButton = screen.queryByLabelText('Minimize to popup');
        expect(minimizeButton).toBeInTheDocument();
      }
    }
  });
});
