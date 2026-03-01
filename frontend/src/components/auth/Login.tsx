// src/components/auth/Login.tsx

import React, { useState, useEffect } from 'react';
import { useAuth } from '../../contexts/AuthContext';
import { isValidNeptunCode } from '../../utils/utils';

const Login: React.FC = () => {
    const { login, loading, error, clearError } = useAuth();
    const [neptunCode, setNeptunCode] = useState('');
    const [password, setPassword] = useState('');
    const [validationErrors, setValidationErrors] = useState({
        neptunCode: '',
        password: '',
    });

    useEffect(() => {
        return () => clearError();
    }, [clearError]);

    const validateForm = (): boolean => {
        const errors = {
            neptunCode: '',
            password: '',
        };

        if (!neptunCode.trim()) {
            errors.neptunCode = 'Neptun code is required';
        } else if (!isValidNeptunCode(neptunCode)) {
            errors.neptunCode = 'Invalid Neptun code format';
        }

        if (!password.trim()) {
            errors.password = 'Password is required';
        } else if (password.length < 6) {
            errors.password = 'Password must be at least 6 characters';
        }

        setValidationErrors(errors);
        return !errors.neptunCode && !errors.password;
    };

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        clearError();

        if (!validateForm()) {
            return;
        }

        try {
            await login({ neptunCode, password });
        } catch (err) {
            console.error('Login failed:', err);
        }
    };

    const handleNeptunCodeChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const value = e.target.value.toUpperCase();
        setNeptunCode(value);
        if (validationErrors.neptunCode) {
            setValidationErrors((prev) => ({ ...prev, neptunCode: '' }));
        }
    };

    const handlePasswordChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        setPassword(e.target.value);
        if (validationErrors.password) {
            setValidationErrors((prev) => ({ ...prev, password: '' }));
        }
    };

    const handleKeyPress = (e: React.KeyboardEvent) => {
        if (e.key === 'Enter') {
            handleSubmit(e as any);
        }
    };

    return (
        <div className="min-h-screen bg-[#F5F7FA] flex items-center justify-center p-4">
            <div className="bg-white rounded-lg shadow-lg p-8 w-full max-w-md border border-gray-200">
                <div className="text-center mb-8">
                    <div className="inline-block bg-[#2C3E50] rounded-lg p-4 mb-4">
                        <h1 className="text-3xl font-bold text-white">
                            Neptun
                        </h1>
                    </div>
                    <h2 className="text-2xl font-bold text-gray-900 mb-2">Connect</h2>
                    <p className="text-gray-600">Sign in to your account</p>
                </div>

                <form onSubmit={handleSubmit} className="space-y-5">
                    <div>
                        <label
                            htmlFor="neptunCode"
                            className="block text-sm font-semibold text-gray-700 mb-2"
                        >
                            Neptun Code
                        </label>
                        <input
                            id="neptunCode"
                            type="text"
                            value={neptunCode}
                            onChange={handleNeptunCodeChange}
                            onKeyPress={handleKeyPress}
                            placeholder="ABC123"
                            maxLength={6}
                            disabled={loading}
                            className={`w-full px-4 py-3 border rounded-md focus:ring-2 focus:ring-[#3498DB] focus:border-[#3498DB] focus:outline-none transition-all ${
                                validationErrors.neptunCode
                                    ? 'border-red-500'
                                    : 'border-gray-300'
                            } ${loading ? 'bg-gray-100 cursor-not-allowed' : 'bg-white'}`}
                        />
                        {validationErrors.neptunCode && (
                            <p className="mt-1 text-sm text-red-600">
                                {validationErrors.neptunCode}
                            </p>
                        )}
                    </div>

                    <div>
                        <label
                            htmlFor="password"
                            className="block text-sm font-semibold text-gray-700 mb-2"
                        >
                            Password
                        </label>
                        <input
                            id="password"
                            type="password"
                            value={password}
                            onChange={handlePasswordChange}
                            onKeyPress={handleKeyPress}
                            placeholder="Enter your password"
                            disabled={loading}
                            className={`w-full px-4 py-3 border rounded-md focus:ring-2 focus:ring-[#3498DB] focus:border-[#3498DB] focus:outline-none transition-all ${
                                validationErrors.password
                                    ? 'border-red-500'
                                    : 'border-gray-300'
                            } ${loading ? 'bg-gray-100 cursor-not-allowed' : 'bg-white'}`}
                        />
                        {validationErrors.password && (
                            <p className="mt-1 text-sm text-red-600">
                                {validationErrors.password}
                            </p>
                        )}
                    </div>

                    {error && (
                        <div className="bg-red-50 border border-red-300 text-red-700 px-4 py-3 rounded-md text-sm">
                            {error}
                        </div>
                    )}

                    <button
                        type="submit"
                        disabled={loading}
                        className="w-full bg-[#3498DB] text-white py-3 px-4 rounded-md hover:bg-[#2980B9] transition-colors disabled:bg-gray-400 disabled:cursor-not-allowed font-semibold shadow-sm"
                    >
                        {loading ? (
                            <span className="flex items-center justify-center">
                <svg
                    className="animate-spin h-5 w-5 mr-2"
                    viewBox="0 0 24 24"
                    fill="none"
                >
                  <circle
                      className="opacity-25"
                      cx="12"
                      cy="12"
                      r="10"
                      stroke="currentColor"
                      strokeWidth="4"
                  />
                  <path
                      className="opacity-75"
                      fill="currentColor"
                      d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"
                  />
                </svg>
                Signing in...
              </span>
                        ) : (
                            'Sign in'
                        )}
                    </button>
                </form>

                <div className="mt-6 text-center text-sm text-gray-600 bg-gray-50 p-3 rounded-md border border-gray-200">
                    <p className="font-semibold text-gray-700 mb-2">Demo accounts:</p>
                    <p className="font-mono text-xs">ABC123 / password</p>
                    <p className="font-mono text-xs">BLOMOE / password</p>
                </div>
            </div>
        </div>
    );
};

export default Login;