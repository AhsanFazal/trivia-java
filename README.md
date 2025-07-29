# Trivia Game Application

A web-based trivia game that fetches questions from the Open Trivia Database API and provides a secure backend to prevent answer exposure to clients.

## Features

- Fetch trivia questions from various categories and difficulties
- Secure answer validation (answers never sent to client)
- Session-based game state management
- RESTful API

## Architecture

### Backend (Spring Boot)

- **Controller Layer**: REST endpoints for questions and answer validation
- **Service Layer**: Business logic and session management
- **Client Layer**: Integration with Open Trivia API
- **Exception Handling**: Global exception handler for consistent error responses

### Frontend (Vanilla JavaScript)

- Single-page application with multiple screens
- Responsive design for mobile and desktop
- Real-time progress tracking
- Clear result visualization

## Prerequisites

- Java 21 or higher
- Maven 3.6 or higher
- Internet connection (for Open Trivia API)

## Installation and Running

```bash
# Clone the repository
git clone <repository-url>
cd trivia-java

# Run the application
./mvnw spring-boot:run

# For Windows
mvnw.cmd spring-boot:run
```

The application will start on <http://localhost:8080>

## API

### GET /api/questions

Fetch trivia questions for a new game session.

**Parameters:**

- `amount` (optional, default: 10): Number of questions (1-50)
- `category` (optional): Category ID from Open Trivia DB
- `difficulty` (optional): easy, medium, or hard

**Response:**

```json
[
  {
    "id": "unique-question-id",
    "question": "What is the capital of France?",
    "options": ["London", "Berlin", "Paris", "Madrid"],
    "category": "Geography",
    "difficulty": "easy"
  }
]
```

### POST /api/checkanswers

Submit answers for validation.

**Request Body:**

```json
{
  "answers": [
    {
      "questionId": "unique-question-id",
      "answer": "Paris"
    }
  ]
}
```

**Response:**

```json
{
  "results": [
    {
      "questionId": "unique-question-id",
      "correct": true,
      "correctAnswer": "Paris"
    }
  ],
  "totalQuestions": 10,
  "correctAnswers": 8,
  "score": 80.0
}
```

### GET /api/health

Health check endpoint.

**Response:** `OK`

## Configuration

Application properties can be configured in `src/main/resources/application.properties`:

```properties
# Server port
server.port=8080

# Session timeout
server.servlet.session.timeout=30m

# Open Trivia API configuration
trivia.api.url=https://opentdb.com/api.php
trivia.api.timeout=5000
```

## Testing

Run all tests:

```bash
./mvnw test
```

Run with coverage:

```bash
./mvnw test jacoco:report
```

Coverage report will be available at `target/site/jacoco/index.html`

## Design Decisions

### Security

- Answers are stored server-side in the session
- Client never receives correct answers until validation
- Session-based state prevents cheating

### Session Management

- HTTP session stores game state
- Questions cleared after answer submission
- 30-minute session timeout

### Error Handling

- Custom exceptions for different scenarios
- Consistent error response format
- Appropriate HTTP status codes

### Testing Strategy

- Unit tests for business logic
- Integration tests for API endpoints
- MockWebServer for external API testing

## Deployment

### Local Development

The application runs with an embedded Tomcat server on port 8080.

### Production Deployment

Of course, things to consider for production deployment include:

- Using external session storage (Redis)
- Configuring HTTPS
- Setting appropriate CORS origins
- Implementing rate limiting
- Adding monitoring/metrics

And perhaps even things like:

- User authentication and profiles
- Score leaderboards
- Question categories selection
- Multiplayer mode
- WebSocket for real-time features
- Database persistence
- Caching for API responses

### Building for Production

```bash
# Create production JAR
./mvnw clean package

# Run with production profile
java -jar target/trivia-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
```
