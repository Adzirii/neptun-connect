# Neptun Connect

A comprehensive chat and collaboration platform integrated with the Neptun University System, enabling students to communicate, share schedules, and collaborate on coursework.

[![Java](https://img.shields.io/badge/Java-25-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.7-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

---

## Table of Contents

- [Features](#features)
- [Architecture](#architecture)
- [Prerequisites](#prerequisites)
- [Quick Start](#quick-start)
- [Project Structure](#project-structure)
- [Configuration](#configuration)
- [API Documentation](#api-documentation)
- [Development](#development)
- [Testing](#testing)
- [Deployment](#deployment)
- [Troubleshooting](#troubleshooting)
- [Contributing](#contributing)
- [License](#license)

---

## Features

### Neptun Integration
- **Single Sign-On** - Authenticate using Neptun credentials
- **Automatic Data Sync** - All students are automatically synced from Neptun on first startup
- **Timetable Sync** - Access your class schedule directly
- **Course Information** - View enrolled courses and details
- **Exam Tracking** - Get notifications for upcoming exams
- **Grade Monitoring** - Check grades and academic progress

### Chat and Collaboration
- **Real-time Messaging** - WebSocket-powered instant messaging
- **Direct Messages** - One-on-one conversations with classmates
- **Course Channels** - Automatic channels for each enrolled course
- **Study Groups** - Create and join study groups
- **File Sharing** - Share notes, assignments, and materials (images, videos, documents)
- **File Preview** - View images and videos directly in chat
- **Search** - Find messages, users, and conversations
- **Notifications** - Real-time notifications for messages and mentions
- **Presence Tracking** - See who is online
- **Typing Indicators** - Know when someone is typing

### Data Initialization
- **Automatic Student Sync** - On first startup, all students are automatically synced from Neptun
- **12 Pre-configured Students** - Ready to use with credentials (see [DATA_INITIALIZER.md](chat-service/DATA_INITIALIZER.md))
- **Fault Tolerant** - Continues even if some students fail to sync
- **One-time Operation** - Only runs once, skipped on subsequent startups

---

## Architecture

### Multi-Module Maven Project

```
neptun-connect/
├── neptun-mock/          # Mock Neptun API for development and testing
└── chat-service/         # Main chat application
```

### Technology Stack

**Backend**
- Java 25
- Spring Boot 3.5.7
- Spring Security with JWT Authentication
- Spring Data JPA for Database Access
- Spring WebSocket for Real-time Communication
- PostgreSQL for Production Database
- H2 for Development Database
- Redis for Caching Layer
- Flyway for Database Migrations

**Utilities**
- Lombok for Boilerplate Reduction
- MapStruct for DTO Mapping
- SpringDoc OpenAPI for API Documentation
- JUnit 5 and Mockito for Testing

**Deployment**
- Docker and Docker Compose
- GitHub Actions for CI/CD

---

## Prerequisites

### Required Software

- **Java Development Kit (JDK) 25** or higher
    - Download: [Oracle JDK](https://www.oracle.com/java/technologies/downloads/) or [OpenJDK](https://openjdk.org/)
- **Maven 3.9+**
    - Download: [Apache Maven](https://maven.apache.org/download.cgi)
- **Docker and Docker Compose** for containerized deployment
    - Download: [Docker Desktop](https://www.docker.com/products/docker-desktop)
- **Git**
    - Download: [Git SCM](https://git-scm.com/downloads)

### Optional Tools

- **IntelliJ IDEA** or **Eclipse** for IDE
- **Postman** or **Insomnia** for API Testing
- **Redis** if running without Docker
- **PostgreSQL** if running without Docker

### Verify Installation

```bash
java -version          # Should show Java 25
mvn -version           # Should show Maven 3.9+
docker --version       # Should show Docker version
docker-compose --version
```

---

## Quick Start

### 1. Clone the Repository

```bash
git clone https://github.com/Adzirii/neptun-connect.git
cd neptun-connect
```

### 2. Build the Project

```bash
mvn clean install
```

### 3. Run with Docker Compose (Recommended)

```bash
docker-compose up -d
```

This will start:
- Neptun Mock API on port 8081
- Chat Service on port 8080
- PostgreSQL on port 5432
- Redis on port 6379

**Note:** On first startup, Chat Service will automatically sync all 12 students from Neptun Mock API. This process takes about 3-5 seconds.

### 4. Run Without Docker

**Terminal 1 - Start Neptun Mock API:**
```bash
cd neptun-mock
mvn spring-boot:run
```

**Terminal 2 - Start Chat Service:**
```bash
cd chat-service
mvn spring-boot:run
```

**Important:** Make sure Neptun Mock API is running before starting Chat Service, as it needs to sync student data on first startup.

### 5. Verify Services

- Neptun Mock API: http://localhost:8081/api2
- Chat Service: http://localhost:8080/api1
- Swagger UI (Chat Service): http://localhost:8080/swagger-ui.html
- Swagger UI (Neptun Mock): http://localhost:8081/swagger-ui.html

### 6. Test with Demo Accounts

All students use the same password: **password**

Available accounts:
- ABC123 / password (John Doe)
- DEF456 / password (Jane Smith)
- GHI789 / password (Peter Nagy)
- BLOMOE / password (Nikita Liubov)
- JKL012 / password (Anna Horvath)
- MNO345 / password (Balazs Szabo)
- PQR678 / password (Eva Kiss)
- STU901 / password (Gabor Farkas)
- VWX234 / password (Katalin Molnar)
- YZA567 / password (Laszlo Varga)
- BCD890 / password (Zsofia Nemeth)
- EFG123 / password (Tamas Kovacs)

For more details about data initialization, see [DATA_INITIALIZER.md](chat-service/DATA_INITIALIZER.md)

---

## Project Structure

```
neptun-connect/
│
├── pom.xml                           # Parent POM with dependency management
│
├── neptun-mock/                      # Neptun API Mock Module
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   │   └── com/thesis/neptunmock/
│   │   │   │       ├── NeptunMockApplication.java
│   │   │   │       ├── config/                 # Security, JWT, CORS config
│   │   │   │       ├── controller/             # REST Controllers
│   │   │   │       ├── dto/                    # Data Transfer Objects
│   │   │   │       ├── model/                  # Domain Models
│   │   │   │       ├── service/                # Business Logic
│   │   │   │       ├── repository/             # Data Access Layer
│   │   │   │       └── exception/              # Custom Exceptions
│   │   │   └── resources/
│   │   │       ├── application.properties      # Configuration
│   │   │       └── data.sql                    # Mock Data
│   │   └── test/                               # Unit and Integration Tests
│   └── pom.xml
│
├── chat-service/                     # Chat Service Module
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   │   └── com/thesis/chatservice/
│   │   │   │       ├── ChatServiceApplication.java
│   │   │   │       ├── config/                 # WebSocket, Security config
│   │   │   │       ├── controller/             # REST and WebSocket Controllers
│   │   │   │       ├── dto/                    # Data Transfer Objects
│   │   │   │       ├── entity/                 # JPA Entities
│   │   │   │       ├── service/                # Business Logic
│   │   │   │       ├── repository/             # JPA Repositories
│   │   │   │       ├── client/                 # Neptun API Client
│   │   │   │       ├── mapper/                 # MapStruct Mappers
│   │   │   │       ├── security/               # JWT, Authentication
│   │   │   │       └── exception/              # Exception Handlers
│   │   │   └── resources/
│   │   │       ├── application.properties
│   │   │       ├── application-dev.properties
│   │   │       ├── application-prod.properties
│   │   │       └── db/migration/               # Flyway Migrations
│   │   └── test/
│   └── pom.xml
│
├── docker-compose.yml                # Docker Compose Configuration
├── .gitignore
└── README.md
```

---

## Configuration

### Application Properties

#### Neptun Mock (neptun-mock/src/main/resources/application.properties)

```properties
# Server Configuration
spring.application.name=neptun-mock
server.port=8081
server.servlet.context-path=/neptun

# JWT Configuration
jwt.secret=your-secret-key-min-256-bits
jwt.expiration=86400000

# Logging
logging.level.com.thesis.neptunmock=DEBUG
```

#### Chat Service (chat-service/src/main/resources/application.properties)

```properties
# Server Configuration
spring.application.name=chat-service
server.port=8080

# Database Configuration (H2 for dev)
spring.datasource.url=jdbc:h2:mem:chatdb
spring.datasource.driver-class-name=org.h2.Driver
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.h2.console.enabled=true

# Flyway
spring.flyway.enabled=true

# Neptun API Client
neptun.api.base-url=http://localhost:8081/neptun

# JWT Configuration
jwt.secret=your-secret-key-min-256-bits
jwt.expiration=86400000

# WebSocket Configuration
websocket.allowed-origins=http://localhost:3000

# File Upload
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB

# Logging
logging.level.com.thesis.chatservice=DEBUG
```

#### Production Configuration (application-prod.properties)

```properties
# PostgreSQL Configuration
spring.datasource.url=jdbc:postgresql://localhost:5432/neptun_chat
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.hibernate.ddl-auto=validate

# Redis Configuration
spring.data.redis.host=${REDIS_HOST:localhost}
spring.data.redis.port=${REDIS_PORT:6379}

# Production Security
jwt.secret=${JWT_SECRET}
```

### Environment Variables

Create a `.env` file in the root directory:

```bash
# Database
DB_USERNAME=neptun_user
DB_PASSWORD=secure_password
DB_NAME=neptun_chat

# Redis
REDIS_HOST=localhost
REDIS_PORT=6379

# JWT
JWT_SECRET=your-very-secure-secret-key-at-least-256-bits-long

# Neptun API
NEPTUN_API_URL=http://neptun-mock:8081/neptun
```

---

## API Documentation

### Access Swagger UI

After starting the services, access the interactive API documentation:

- **Chat Service**: http://localhost:8080/swagger-ui.html
- **Neptun Mock**: http://localhost:8081/swagger-ui.html

### Key Endpoints

#### Authentication

```
POST /api/auth/login
POST /api/auth/refresh
GET  /api/auth/profile
```

#### Users

```
GET    /api/users/profile
PUT    /api/users/profile
GET    /api/users/search?query={query}
GET    /api/users/{userId}
```

#### Conversations

```
GET    /api/conversations
POST   /api/conversations
GET    /api/conversations/{id}
DELETE /api/conversations/{id}
POST   /api/conversations/{id}/participants
DELETE /api/conversations/{id}/participants/{userId}
```

#### Messages

```
GET    /api/conversations/{conversationId}/messages
POST   /api/conversations/{conversationId}/messages
PUT    /api/messages/{messageId}
DELETE /api/messages/{messageId}
POST   /api/messages/{messageId}/read
```

#### Course Channels

```
GET    /api/channels/courses
GET    /api/channels/{courseCode}
GET    /api/channels/{courseCode}/messages
```

#### WebSocket Endpoints

```
CONNECT /ws
SUBSCRIBE /topic/conversations/{conversationId}
SUBSCRIBE /user/queue/notifications
SEND /app/chat/{conversationId}
SEND /app/typing/{conversationId}
```

---

## Development

### Setting Up Development Environment

**1. Import Project to IDE**

For IntelliJ IDEA:
- File > Open > Select root `pom.xml`
- Enable annotation processing for Lombok
- Settings > Build > Compiler > Annotation Processors > Enable

For Eclipse:
- File > Import > Existing Maven Projects
- Install Lombok plugin

**2. Run in Development Mode**

```bash
# Run with dev profile
cd chat-service
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

**3. Hot Reload with Spring DevTools**

Spring DevTools is included for automatic application restart on code changes.

### Code Style

- Follow Java naming conventions
- Use Lombok annotations to reduce boilerplate
- Write meaningful commit messages
- Keep methods focused and concise
- Document complex business logic

### Database Migrations

Create new migration in `chat-service/src/main/resources/db/migration/`:

```sql
-- V1__initial_schema.sql
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    neptun_code VARCHAR(6) UNIQUE NOT NULL,
    email VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

Migrations run automatically on application start.

### Adding New Dependencies

Update the parent `pom.xml` in `<dependencyManagement>` section:

```xml
<dependency>
    <groupId>com.example</groupId>
    <artifactId>library</artifactId>
    <version>1.0.0</version>
</dependency>
```

Then add to child module `pom.xml` without version:

```xml
<dependency>
    <groupId>com.example</groupId>
    <artifactId>library</artifactId>
</dependency>
```

---

## Testing

### Run All Tests

```bash
mvn clean test
```

### Run Tests for Specific Module

```bash
cd chat-service
mvn test
```

### Run Integration Tests

```bash
mvn verify
```

### Test Coverage

```bash
mvn clean test jacoco:report
```

View coverage report at `target/site/jacoco/index.html`

### Writing Tests

**Unit Test Example:**

```java
@ExtendWith(MockitoExtension.class)
class MessageServiceTest {
    
    @Mock
    private MessageRepository messageRepository;
    
    @InjectMocks
    private MessageService messageService;
    
    @Test
    void shouldCreateMessage() {
        // given
        MessageDto messageDto = new MessageDto();
        messageDto.setContent("Test message");
        
        // when
        Message result = messageService.createMessage(messageDto);
        
        // then
        assertNotNull(result);
        assertEquals("Test message", result.getContent());
    }
}
```

**Integration Test Example:**

```java
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class ConversationControllerIntegrationTest {
    
    @Autowired
    private TestRestTemplate restTemplate;
    
    @Test
    void shouldGetConversations() {
        ResponseEntity<ConversationDto[]> response = 
            restTemplate.getForEntity("/api/conversations", ConversationDto[].class);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}
```

---

## Deployment

### Docker Deployment

**1. Build Docker Images**

```bash
docker-compose build
```

**2. Start Services**

```bash
docker-compose up -d
```

**3. View Logs**

```bash
docker-compose logs -f chat-service
docker-compose logs -f neptun-mock
```

**4. Stop Services**

```bash
docker-compose down
```

**5. Clean Up (including volumes)**

```bash
docker-compose down -v
```

### Production Deployment

**1. Build Production JARs**

```bash
mvn clean package -Pprod
```

**2. Run with Production Profile**

```bash
java -jar chat-service/target/chat-service-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
```

**3. Environment Variables**

Set all required environment variables from `.env` file in your deployment environment.

### Kubernetes Deployment

Create deployment files in `k8s/` directory:

- `postgres-deployment.yaml`
- `redis-deployment.yaml`
- `neptun-mock-deployment.yaml`
- `chat-service-deployment.yaml`
- `ingress.yaml`

Deploy:

```bash
kubectl apply -f k8s/
```

---

## Troubleshooting

### Common Issues

**Issue: Port Already in Use**

```bash
# Find process using port 8080
lsof -i :8080

# Kill the process
kill -9 <PID>
```

**Issue: Maven Build Fails**

```bash
# Clean and rebuild
mvn clean install -U

# Skip tests if needed
mvn clean install -DskipTests
```

**Issue: Database Connection Failed**

- Verify PostgreSQL is running
- Check credentials in application.properties
- Ensure database exists

**Issue: JWT Token Invalid**

- Verify JWT secret is set and matches between services
- Check token expiration time
- Ensure clock synchronization

**Issue: WebSocket Connection Failed**

- Check CORS configuration
- Verify WebSocket endpoint URL
- Check firewall rules

### Logging

Enable debug logging:

```properties
logging.level.com.thesis=DEBUG
logging.level.org.springframework.security=DEBUG
logging.level.org.springframework.web=DEBUG
```

---

## Contributing

### How to Contribute

1. Fork the repository
2. Create a feature branch: `git checkout -b feature/amazing-feature`
3. Commit your changes: `git commit -m 'Add amazing feature'`
4. Push to the branch: `git push origin feature/amazing-feature`
5. Open a Pull Request

### Coding Guidelines

- Write clear, self-documenting code
- Add unit tests for new features
- Update documentation as needed
- Follow existing code style
- Keep commits atomic and well-described

### Commit Message Format

```
type(scope): subject

body

footer
```

Types: `feat`, `fix`, `docs`, `style`, `refactor`, `test`, `chore`

Example:
```
feat(chat): add message editing functionality

Users can now edit their sent messages within 5 minutes.
Edited messages are marked with an indicator.

Closes #123
```

---

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

## Contact and Support

- **Project Repository**: https://github.com/Adzirii/neptun-connect
- **Issue Tracker**: https://github.com/Adzirii/neptun-connect/issues
- **Documentation**: https://github.com/Adzirii/neptun-connect/wiki

---

## Acknowledgments

- Spring Framework Team
- Neptun University System
- All contributors to this project

---

## Roadmap

### Version 1.0 (Current Development)
- Core messaging functionality
- Neptun integration
- Course channels
- Basic file sharing

### Version 1.1 (Planned)
- Mobile application support
- Advanced search
- Message reactions
- Thread replies

### Version 2.0 (Future)
- Video/Audio calls
- Screen sharing
- AI-powered study assistant
- Integration with other university systems

---

**Built with Spring Boot | Powered by WebSocket | Integrated with Neptun**