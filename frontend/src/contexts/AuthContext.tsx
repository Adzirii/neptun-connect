import React, { createContext, useContext, useState, useEffect, ReactNode } from 'react';
import { User, LoginRequest, ErrorResponse } from '../types/types';
import apiClient from '../api/apiClient';

interface AuthContextType {
    user: User | null;
    loading: boolean;
    error: string | null;
    isAuthenticated: boolean;
    login: (credentials: LoginRequest) => Promise<void>;
    logout: () => void;
    syncUser: () => Promise<void>;
    clearError: () => void;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

interface AuthProviderProps {
    children: ReactNode;
}

export const AuthProvider: React.FC<AuthProviderProps> = ({ children }) => {
    const [user, setUser] = useState<User | null>(null);
    const [loading, setLoading] = useState<boolean>(true);
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        checkAuth();

        const handleUnauthorized = () => {
            setUser(null);
            setError('Session expired. Please login again.');
        };

        window.addEventListener('unauthorized', handleUnauthorized);
        return () => window.removeEventListener('unauthorized', handleUnauthorized);
    }, []);

    const checkAuth = async () => {
        const token = apiClient.getToken();

        if (!token) {
            setLoading(false);
            return;
        }

        try {
            const currentUser = await apiClient.getCurrentUser();
            setUser(currentUser);
            setError(null);
        } catch (err) {
            apiClient.clearToken();
            setUser(null);
        } finally {
            setLoading(false);
        }
    };

    const login = async (credentials: LoginRequest) => {
        setLoading(true);
        setError(null);

        try {
            const response = await apiClient.login(credentials);
            setUser(response.student);

            await syncUser();
        } catch (err) {
            const errorMessage = (err as ErrorResponse).message || 'Login failed';
            setError(errorMessage);
            throw err;
        } finally {
            setLoading(false);
        }
    };

    const logout = () => {
        apiClient.clearToken();
        setUser(null);
        setError(null);
    };

    const syncUser = async () => {
        try {
            const syncedUser = await apiClient.syncUser();
            setUser(syncedUser);
        } catch (err) {
            console.error('Failed to sync user:', err);
        }
    };

    const clearError = () => {
        setError(null);
    };

    const value: AuthContextType = {
        user,
        loading,
        error,
        isAuthenticated: !!user,
        login,
        logout,
        syncUser,
        clearError,
    };

    return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
};

export const useAuth = (): AuthContextType => {
    const context = useContext(AuthContext);
    if (!context) {
        throw new Error('useAuth must be used within AuthProvider');
    }
    return context;
};
