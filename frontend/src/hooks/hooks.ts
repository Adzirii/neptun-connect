
import { useState, useEffect, useRef, useCallback } from 'react';
import { User } from '../types/types';
import apiClient from '../api/apiClient';
import { debounce } from '../utils/utils';

/**
 * Hook for searching users with debouncing
 */
export const useUserSearch = (delay: number = 300) => {
    const [query, setQuery] = useState<string>('');
    const [results, setResults] = useState<User[]>([]);
    const [loading, setLoading] = useState<boolean>(false);
    const [error, setError] = useState<string | null>(null);

    const debouncedSearch = useRef(
        debounce(async (searchQuery: string) => {
            if (!searchQuery.trim()) {
                setResults([]);
                setLoading(false);
                return;
            }

            setLoading(true);
            setError(null);

            try {
                const users = await apiClient.searchUsers(searchQuery);
                setResults(users);
            } catch (err) {
                setError('Failed to search users');
                console.error('Search error:', err);
            } finally {
                setLoading(false);
            }
        }, delay)
    ).current;

    useEffect(() => {
        debouncedSearch(query);
    }, [query, debouncedSearch]);

    const clearResults = () => {
        setQuery('');
        setResults([]);
        setError(null);
    };

    return {
        query,
        setQuery,
        results,
        loading,
        error,
        clearResults,
    };
};

/**
 * Hook for managing scroll position
 */
export const useScrollPosition = () => {
    const [isAtBottom, setIsAtBottom] = useState<boolean>(true);
    const containerRef = useRef<HTMLDivElement>(null);

    const scrollToBottom = useCallback((behavior: ScrollBehavior = 'smooth') => {
        if (containerRef.current) {
            containerRef.current.scrollTo({
                top: containerRef.current.scrollHeight,
                behavior,
            });
        }
    }, []);

    const handleScroll = useCallback(() => {
        if (!containerRef.current) return;

        const { scrollTop, scrollHeight, clientHeight } = containerRef.current;
        const threshold = 100;
        const atBottom = scrollHeight - scrollTop - clientHeight < threshold;

        setIsAtBottom(atBottom);
    }, []);

    useEffect(() => {
        const container = containerRef.current;
        if (!container) return;

        container.addEventListener('scroll', handleScroll);
        return () => container.removeEventListener('scroll', handleScroll);
    }, [handleScroll]);

    return {
        containerRef,
        isAtBottom,
        scrollToBottom,
    };
};

/**
 * Hook for managing modal state
 */
export const useModal = (initialState: boolean = false) => {
    const [isOpen, setIsOpen] = useState<boolean>(initialState);

    const open = useCallback(() => setIsOpen(true), []);
    const close = useCallback(() => setIsOpen(false), []);
    const toggle = useCallback(() => setIsOpen((prev) => !prev), []);

    return {
        isOpen,
        open,
        close,
        toggle,
    };
};

/**
 * Hook for detecting clicks outside element
 */
export const useClickOutside = <T extends HTMLElement = HTMLElement>(
    ref: React.RefObject<T | null>,
    handler: () => void
) => {
    useEffect(() => {
        const listener = (event: MouseEvent | TouchEvent) => {
            const el = ref.current;
            if (!el || el.contains(event.target as Node)) {
                return;
            }
            handler();
        };

        document.addEventListener('mousedown', listener);
        document.addEventListener('touchstart', listener);

        return () => {
            document.removeEventListener('mousedown', listener);
            document.removeEventListener('touchstart', listener);
        };
    }, [ref, handler]);
};

/**
 * Hook for copying text to clipboard
 */
export const useClipboard = (timeout: number = 2000) => {
    const [copied, setCopied] = useState<boolean>(false);

    const copy = useCallback(
        async (text: string) => {
            try {
                await navigator.clipboard.writeText(text);
                setCopied(true);
                setTimeout(() => setCopied(false), timeout);
                return true;
            } catch (err) {
                console.error('Failed to copy:', err);
                return false;
            }
        },
        [timeout]
    );

    return { copied, copy };
};

/**
 * Hook for managing loading states
 */
export const useLoading = (initialState: boolean = false) => {
    const [loading, setLoading] = useState<boolean>(initialState);

    const startLoading = useCallback(() => setLoading(true), []);
    const stopLoading = useCallback(() => setLoading(false), []);

    const withLoading = useCallback(
        async <T,>(promise: Promise<T>): Promise<T> => {
            startLoading();
            try {
                return await promise;
            } finally {
                stopLoading();
            }
        },
        [startLoading, stopLoading]
    );

    return {
        loading,
        startLoading,
        stopLoading,
        withLoading,
    };
};

/**
 * Hook for managing form state
 */
export const useForm = <T extends Record<string, any>>(initialValues: T) => {
    const [values, setValues] = useState<T>(initialValues);
    const [errors, setErrors] = useState<Partial<Record<keyof T, string>>>({});
    const [touched, setTouched] = useState<Partial<Record<keyof T, boolean>>>({});

    const handleChange = useCallback(
        (field: keyof T) => (value: any) => {
            setValues((prev) => ({ ...prev, [field]: value }));
            setTouched((prev) => ({ ...prev, [field]: true }));
        },
        []
    );

    const setFieldValue = useCallback((field: keyof T, value: any) => {
        setValues((prev) => ({ ...prev, [field]: value }));
    }, []);

    const setFieldError = useCallback((field: keyof T, error: string) => {
        setErrors((prev) => ({ ...prev, [field]: error }));
    }, []);

    const clearFieldError = useCallback((field: keyof T) => {
        setErrors((prev) => {
            const newErrors = { ...prev };
            delete newErrors[field];
            return newErrors;
        });
    }, []);

    const reset = useCallback(() => {
        setValues(initialValues);
        setErrors({});
        setTouched({});
    }, [initialValues]);

    const isValid = Object.keys(errors).length === 0;

    return {
        values,
        errors,
        touched,
        handleChange,
        setFieldValue,
        setFieldError,
        clearFieldError,
        reset,
        isValid,
    };
};

/**
 * Hook for managing local storage
 */
export const useLocalStorage = <T,>(
    key: string,
    initialValue: T
): [T, (value: T) => void, () => void] => {
    const [storedValue, setStoredValue] = useState<T>(() => {
        try {
            const item = window.localStorage.getItem(key);
            return item ? JSON.parse(item) : initialValue;
        } catch (error) {
            console.error('Error reading from localStorage:', error);
            return initialValue;
        }
    });

    const setValue = (value: T) => {
        try {
            setStoredValue(value);
            window.localStorage.setItem(key, JSON.stringify(value));
        } catch (error) {
            console.error('Error writing to localStorage:', error);
        }
    };

    const removeValue = () => {
        try {
            window.localStorage.removeItem(key);
            setStoredValue(initialValue);
        } catch (error) {
            console.error('Error removing from localStorage:', error);
        }
    };

    return [storedValue, setValue, removeValue];
};

/**
 * Hook for managing window size
 */
export const useWindowSize = () => {
    const [windowSize, setWindowSize] = useState({
        width: window.innerWidth,
        height: window.innerHeight,
    });

    useEffect(() => {
        const handleResize = () => {
            setWindowSize({
                width: window.innerWidth,
                height: window.innerHeight,
            });
        };

        window.addEventListener('resize', handleResize);
        return () => window.removeEventListener('resize', handleResize);
    }, []);

    return windowSize;
};
