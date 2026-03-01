interface Config {
    apiBaseUrl: string;
    neptunApiUrl: string;
    wsBaseUrl: string;
    environment: 'development' | 'production';
}

const development: Config = {
    apiBaseUrl: 'http://localhost:8080/api',
    neptunApiUrl: 'http://localhost:8081/neptun/api',
    wsBaseUrl: 'ws://localhost:8080/ws',
    environment: 'development',
};

const production: Config = {
    apiBaseUrl: process.env.REACT_APP_API_URL || '/api',
    neptunApiUrl: process.env.REACT_APP_NEPTUN_API_URL || '/neptun/api',
    wsBaseUrl: process.env.REACT_APP_WS_URL || 'ws://localhost:8080/ws',
    environment: 'production',
};

const config: Config = process.env.NODE_ENV === 'production' ? production : development;

export default config;