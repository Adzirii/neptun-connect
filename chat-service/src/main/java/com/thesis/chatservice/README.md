# Chat Service

Chat service module for Neptun Connect - Real-time messaging and collaboration platform.

## Prerequisites

- Java 25 or higher
- Maven 3.9+
- Running Neptun Mock API (port 8081)

## Quick Start

### 1. Build the Project

```bash
cd chat-service
mvn clean install
```

### 2. Run in Development Mode

```bash
mvn spring-boot:run
```

The service will start on port 8080 by default.

### 3. Access the Application

- API Base URL: http://localhost:8080/api
- Swagger UI: http://localhost:8080/swagger-ui.html
- H2 Console: http://localhost:8080/h2-console
    - JDBC URL: jdbc:h2:mem:chatdb
    - Username: sa
    - Password: (empty)

## Configuration

### Development Profile (default)

Uses H2 in-memory database. Configured in `application-dev.yml`.

### Production Profile

Uses PostgreSQL. Set environment variables:

```bash
export SPRING_PROFILES_ACTIVE=prod
export DB_HOST=localhost
export DB_PORT=5432
export DB_NAME=neptun_chat
export DB_USERNAME=neptun_user
export DB_PASSWORD=your_password
export JWT_SECRET=your-secret-key
```

## API Usage

### 1. Authentication

First, obtain a JWT token from Neptun Mock API:

```bash
curl -X POST http://localhost:8081/neptun/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "neptunCode": "ABC123",
    "password": "password123"
  }'
```

Response:
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "tokenType": "Bearer",
  "expiresIn": 86400000,
  "student": { ... }
}
```

### 2. Use the Token

Include the token in all requests to Chat Service:

```bash
curl -X GET http://localhost:8080/api/auth/profile \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"
```

## Main Endpoints

### Authentication
- `GET /api/auth/profile` - Get current user profile
- `POST /api/auth/sync` - Sync user with Neptun

### Users
- `GET /api/users` - Get all active users
- `GET /api/users/{userId}` - Get user by ID
- `GET /api/users/search?query={query}` - Search users

### Conversations
- `GET /api/conversations` - Get user's conversations
- `POST /api/conversations` - Create new conversation
- `GET /api/conversations/{id}` - Get conversation details
- `POST /api/conversations/{id}/participants/{userId}` - Add participant
- `DELETE /api/conversations/{id}/participants/{userId}` - Remove participant

### Messages
- `GET /api/messages/conversation/{id}` - Get conversation messages
- `POST /api/messages` - Send message
- `PUT /api/messages/{id}` - Update message
- `DELETE /api/messages/{id}` - Delete message
- `POST /api/messages/{id}/read` - Mark message as read
- `GET /api/messages/conversation/{id}/search?query={query}` - Search messages

## Database Schema

### Users Table
Stores user information synchronized from Neptun.

### Conversations Table
Stores conversations (DIRECT, GROUP, CHANNEL, COURSE).

### Messages Table
Stores all messages with support for replies and attachments.

### Conversation Participants
Tracks users in conversations with roles (OWNER, ADMIN, MEMBER).

### Message Read Status
Tracks which users have read which messages.

## Development

### Running Tests

```bash
mvn test
```

### Code Style

- Use Lombok annotations to reduce boilerplate
- Follow Java naming conventions
- Write meaningful log messages
- Add JavaDoc for complex methods

### Database Migrations

Flyway migrations are located in `src/main/resources/db/migration/`.

To add a new migration:
1. Create file: `V{version}__{description}.sql`
2. Example: `V2__add_reactions_table.sql`
3. Restart application - migration runs automatically

## Troubleshooting

### Port Already in Use

```bash
# Find process using port 8080
lsof -i :8080
# Kill the process
kill -9 
```

### Database Connection Issues

- Check H2 console at http://localhost:8080/h2-console
- Verify JDBC URL: `jdbc:h2:mem:chatdb`
- Check logs for Flyway migration errors

### JWT Token Issues

- Ensure Neptun Mock API is running
- Verify token is not expired
- Check JWT secret matches between services

## Project Structure

```
chat-service/
├── src/
│   ├── main/
│   │   ├── java/com/thesis/chatservice/
│   │   │   ├── ChatServiceApplication.java
│   │   │   ├── config/          # Configuration classes
│   │   │   ├── controller/      # REST controllers
│   │   │   ├── dto/            # Data Transfer Objects
│   │   │   ├── entity/         # JPA entities
│   │   │   ├── repository/     # Data access layer
│   │   │   ├── service/        # Business logic
│   │   │   ├── security/       # JWT and security
│   │   │   ├── client/         # Neptun API client
│   │   │   └── exception/      # Exception handling
│   │   └── resources/
│   │       ├── application.yml
│   │       ├── application-dev.yml
│   │       ├── application-prod.yml
│   │       └── db/migration/   # Flyway scripts
│   └── test/
└── pom.xml
```

## Next Steps

1. Implement WebSocket support for real-time messaging
2. Add file upload functionality
3. Implement message reactions
4. Add search indexing for better search performance
5. Implement notification system
6. Add caching layer with Redis

## License

MIT License