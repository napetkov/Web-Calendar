# Design Document

## Overview

The Web Calendar system is a full-stack application consisting of a Spring Boot backend and a modern web frontend. The backend provides RESTful APIs for user authentication and managing calendar notes with persistent storage in PostgreSQL, while the frontend offers an intuitive calendar interface with speech-to-text capabilities using the Web Speech API.

The system follows a clean architecture pattern with clear separation between the presentation layer (React frontend), API layer (Spring Boot REST controllers), business logic layer (services), and data access layer (JPA repositories). User authentication is implemented using Spring Security with JWT tokens to ensure each user has their own private calendar.

## Architecture

### System Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                        Frontend Layer                        │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────────┐  │
│  │    Login/    │  │   Calendar   │  │  Note Input      │  │
│  │   Register   │  │  Component   │  │  Component       │  │
│  └──────────────┘  └──────────────┘  └──────────────────┘  │
│  ┌──────────────────────────────────────────────────────┐  │
│  │         Speech-to-Text Component                      │  │
│  └──────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────┘
                            │ HTTP/REST + JWT
┌─────────────────────────────────────────────────────────────┐
│                      Backend Layer (Spring Boot)             │
│  ┌──────────────────────────────────────────────────────┐   │
│  │         Spring Security + JWT Filter                  │   │
│  └──────────────────────────────────────────────────────┘   │
│                            │                                 │
│  ┌──────────────────────────────────────────────────────┐   │
│  │              REST Controllers                         │   │
│  │    (AuthController, NoteController)                  │   │
│  └──────────────────────────────────────────────────────┘   │
│                            │                                 │
│  ┌──────────────────────────────────────────────────────┐   │
│  │              Service Layer                            │   │
│  │      (AuthService, NoteService, UserService)         │   │
│  └──────────────────────────────────────────────────────┘   │
│                            │                                 │
│  ┌──────────────────────────────────────────────────────┐   │
│  │         Repository Layer (JPA)                        │   │
│  │      (UserRepository, NoteRepository)                │   │
│  └──────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────┘
                            │
┌─────────────────────────────────────────────────────────────┐
│                    Database Layer (PostgreSQL)               │
└─────────────────────────────────────────────────────────────┘
```

### Technology Stack

**Backend:**
- Spring Boot 3.x
- Spring Security with JWT authentication
- Spring Data JPA
- PostgreSQL database (development and production)
- Gradle for dependency management
- Java 17+
- BCrypt for password hashing

**Frontend:**
- React 18+
- React Router for navigation
- Web Speech API for speech-to-text
- Axios for HTTP requests
- JWT token management for authentication
- CSS/Tailwind for styling
- Date manipulation library (date-fns or similar)

## Components and Interfaces

### Backend Components

#### 1. User Entity
```java
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String email; // Used as username for login
    
    @Column(nullable = false)
    private String password; // BCrypt hashed
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Note> notes;
}
```

#### 2. Note Entity
```java
@Entity
@Table(name = "notes")
public class Note {
    @Id
    @GeneratedValue
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(nullable = false)
    private LocalDate date;
    
    @Column(nullable = false, length = 2000)
    private String content;
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    @Column
    private LocalDateTime updatedAt;
}
```

#### 3. UserRepository
```java
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
}
```

#### 4. NoteRepository
```java
public interface NoteRepository extends JpaRepository<Note, Long> {
    List<Note> findByUserAndDateOrderByCreatedAtAsc(User user, LocalDate date);
    List<Note> findByUserAndDateBetween(User user, LocalDate startDate, LocalDate endDate);
    List<Note> findByUser(User user);
}
```

#### 5. AuthService
Handles authentication and user management:
- User registration with password hashing
- User login with credential validation
- JWT token generation
- Token validation and user extraction

#### 6. NoteService
Handles business logic for note operations:
- Create note with validation (user-scoped)
- Retrieve notes by date (user-scoped)
- Update existing notes (with ownership verification)
- Delete notes (with ownership verification)
- Validate note content (non-empty, length limits)

#### 7. AuthController
REST endpoints:
- `POST /api/auth/register` - Register new user
- `POST /api/auth/login` - Login and receive JWT token
- `POST /api/auth/logout` - Logout (client-side token removal)

#### 8. NoteController
REST endpoints (all require authentication):
- `POST /api/notes` - Create a new note for authenticated user
- `GET /api/notes?date={date}` - Get notes for a specific date (user's notes only)
- `GET /api/notes/range?start={start}&end={end}` - Get notes for date range (user's notes only)
- `PUT /api/notes/{id}` - Update a note (ownership verified)
- `DELETE /api/notes/{id}` - Delete a note (ownership verified)

### Frontend Components

#### 1. LoginForm Component
- Email and password input fields
- Form validation
- Login button with loading state
- Link to registration page
- Error message display

#### 2. RegisterForm Component
- Email and password input fields
- Password confirmation field
- Form validation (email format, password strength)
- Register button with loading state
- Link to login page
- Error message display

#### 3. CalendarView Component
- Displays monthly calendar grid
- Highlights current date
- Shows visual indicators for dates with notes
- Handles date selection
- Navigation between months
- Logout button

#### 4. NoteInput Component
- Text input field for manual entry
- Speech-to-text button and controls
- Save/Cancel actions
- Real-time character count
- Validation feedback

#### 5. SpeechToText Component
- Integrates Web Speech API
- Microphone activation/deactivation
- Real-time transcription display
- Error handling for unsupported browsers
- Visual feedback during recording

#### 6. NoteList Component
- Displays notes for selected date
- Shows creation/update timestamps
- Edit/Delete actions per note
- Empty state when no notes exist

#### 7. PrivateRoute Component
- Route guard for authenticated pages
- Redirects to login if not authenticated
- Validates JWT token

## Data Models

### User Model

**Fields:**
- `id`: Long (Primary Key, Auto-generated)
- `email`: String (Required, unique, valid email format, used as username)
- `password`: String (Required, BCrypt hashed, min 8 characters)
- `createdAt`: LocalDateTime (Required, auto-set on creation)

**Constraints:**
- Email must be unique and valid format
- Email serves as the username for authentication
- Password must be at least 8 characters before hashing
- Password stored as BCrypt hash

### Note Model

**Fields:**
- `id`: Long (Primary Key, Auto-generated)
- `userId`: Long (Foreign Key, Required)
- `date`: LocalDate (Required, indexed)
- `content`: String (Required, max 2000 characters)
- `createdAt`: LocalDateTime (Required, auto-set on creation)
- `updatedAt`: LocalDateTime (Optional, auto-set on update)

**Constraints:**
- Content must not be empty or only whitespace
- Content length: 1-2000 characters
- Date must be a valid date
- CreatedAt cannot be modified after creation
- Note must belong to a user

### API Request/Response Models

**RegisterRequest:**
```json
{
  "email": "john@example.com",
  "password": "securePassword123"
}
```

**LoginRequest:**
```json
{
  "email": "john@example.com",
  "password": "securePassword123"
}
```

**AuthResponse:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "email": "john@example.com"
}
```

**CreateNoteRequest:**
```json
{
  "date": "2025-11-22",
  "content": "Meeting with team at 2 PM"
}
```

**NoteResponse:**
```json
{
  "id": 1,
  "date": "2025-11-22",
  "content": "Meeting with team at 2 PM",
  "createdAt": "2025-11-22T10:30:00",
  "updatedAt": null
}
```

**UpdateNoteRequest:**
```json
{
  "content": "Meeting with team at 3 PM (updated)"
}
```

## Correctness Properties

*A property is a characteristic or behavior that should hold true across all valid executions of a system—essentially, a formal statement about what the system should do. Properties serve as the bridge between human-readable specifications and machine-verifiable correctness guarantees.*


### Property 1: Month navigation updates calendar view
*For any* valid month and year combination, navigating to that month should update the calendar to display dates for that specific month.
**Validates: Requirements 1.2**

### Property 2: Date selection displays associated notes
*For any* date in the calendar, selecting that date should retrieve and display all notes associated with that date.
**Validates: Requirements 1.3, 2.3, 4.1**

### Property 3: Date selection provides note input interface
*For any* date selected in the calendar, the system should display an input interface for adding new notes.
**Validates: Requirements 2.1**

### Property 4: Note persistence round-trip
*For any* valid note content and date, creating a note should persist it such that retrieving notes for that date returns the saved note with matching content.
**Validates: Requirements 2.2, 6.1**

### Property 5: Empty note rejection
*For any* string composed entirely of whitespace characters (including empty strings), attempting to save it as a note should be rejected and the note should not be persisted.
**Validates: Requirements 2.4**

### Property 6: Notes display with visual indicators
*For any* date that has one or more notes, the calendar view should display a visual indicator on that date.
**Validates: Requirements 2.5**

### Property 7: Speech transcription populates input
*For any* text transcribed by the speech-to-text service, the system should populate the note input field with that transcribed text.
**Validates: Requirements 3.3**

### Property 8: Notes retrieved in chronological order
*For any* date with multiple notes, retrieving notes for that date should return them ordered by creation timestamp (earliest first).
**Validates: Requirements 4.2**

### Property 9: Note display includes content and timestamp
*For any* note displayed in the UI, the display should include both the note content and the creation timestamp.
**Validates: Requirements 4.4**

### Property 10: Existing notes provide edit and delete options
*For any* existing note displayed in the UI, the system should provide both edit and delete action options.
**Validates: Requirements 5.1**

### Property 11: Edit preserves creation timestamp
*For any* note that is edited and saved, the updated note should have modified content but the original creation timestamp should remain unchanged.
**Validates: Requirements 5.2**

### Property 12: Note deletion removes from storage
*For any* note, deleting it should remove it from persistent storage such that subsequent retrieval attempts for that note return no result.
**Validates: Requirements 5.3, 6.4**

### Property 13: Deletion updates calendar view
*For any* date with notes, deleting the last note for that date should remove the visual indicator from the calendar view.
**Validates: Requirements 5.4**

### Property 14: Edit cancellation restores original content
*For any* note being edited, canceling the edit operation should restore the note to its original content before the edit began.
**Validates: Requirements 5.5**

### Property 15: Application restart preserves notes
*For any* set of notes saved before an application restart, all notes should be retrievable after the restart with identical content, dates, and timestamps.
**Validates: Requirements 6.2**

### Property 16: Note updates persist immediately
*For any* note update, immediately retrieving that note should return the updated content, confirming immediate persistence.
**Validates: Requirements 6.3**

### Property 17: User registration creates account with encrypted password
*For any* valid registration information (unique username, valid email, password meeting requirements), the system should create a user account with the password stored as a BCrypt hash, not plaintext.
**Validates: Requirements 7.1**

### Property 18: Valid login credentials authenticate user
*For any* user with a registered account, providing correct username and password should result in successful authentication and return a valid JWT token.
**Validates: Requirements 7.2**

### Property 19: Invalid login credentials are rejected
*For any* login attempt with incorrect username or password, the system should reject authentication and return an error message without revealing which credential was incorrect.
**Validates: Requirements 7.3**

### Property 20: Authenticated users see only their own notes
*For any* authenticated user, retrieving notes for any date should return only notes created by that user, never notes from other users.
**Validates: Requirements 7.4, 8.2**

### Property 21: Notes are associated with creating user
*For any* note created by an authenticated user, the note should be permanently associated with that user's account.
**Validates: Requirements 8.1**

### Property 22: Users cannot access other users' notes
*For any* attempt by a user to update or delete a note belonging to another user, the system should deny access and return an authorization error.
**Validates: Requirements 8.3**

### Property 23: User isolation is maintained across all operations
*For any* two different users with notes on the same date, each user should only be able to retrieve, update, or delete their own notes.
**Validates: Requirements 8.4**

### Property 24: Invalid API requests return proper error codes
*For any* invalid API request (malformed data, missing required fields, invalid date format), the API should return an appropriate HTTP error status code (4xx) and a descriptive error message.
**Validates: Requirements 9.5**

## Error Handling

### Backend Error Handling

**Authentication Errors:**
- Invalid credentials → 401 Unauthorized
- Missing or invalid JWT token → 401 Unauthorized
- Expired JWT token → 401 Unauthorized
- Email already exists → 409 Conflict

**Authorization Errors:**
- Attempting to access another user's note → 403 Forbidden

**Validation Errors:**
- Empty or whitespace-only note content → 400 Bad Request
- Invalid date format → 400 Bad Request
- Note content exceeding 2000 characters → 400 Bad Request
- Missing required fields → 400 Bad Request
- Invalid email format → 400 Bad Request
- Password too short → 400 Bad Request

**Resource Errors:**
- Note not found for update/delete → 404 Not Found
- User not found → 404 Not Found
- Database connection failures → 500 Internal Server Error

**Error Response Format:**
```json
{
  "timestamp": "2025-11-22T10:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Note content cannot be empty",
  "path": "/api/notes"
}
```

### Frontend Error Handling

**Authentication Errors:**
- Invalid login credentials → Display error message on login form
- Registration validation errors → Display field-specific errors
- Email already exists → Display error message
- Session expired → Redirect to login page with message
- Unauthorized access → Redirect to login page

**Speech-to-Text Errors:**
- Browser doesn't support Web Speech API → Display fallback message, show only text input
- Microphone permission denied → Show error message, enable text input
- Speech recognition error → Display error notification, allow retry or manual entry

**API Errors:**
- Network failure → Show retry option with error message
- Validation errors → Display field-specific error messages
- Server errors → Show generic error message with retry option
- Authorization errors → Show "Access denied" message

**User Input Errors:**
- Empty note submission → Inline validation message, prevent submission
- Character limit exceeded → Real-time character count with warning
- Invalid email format → Inline validation on registration
- Weak password → Display password requirements

## Testing Strategy

### Unit Testing

**Backend Unit Tests:**
- NoteService validation logic (empty content, length limits)
- NoteRepository query methods
- Date parsing and formatting utilities
- Error response formatting

**Frontend Unit Tests:**
- Date selection and navigation logic
- Note input validation
- Calendar date calculations
- Component rendering with various props

### Property-Based Testing

The system will use property-based testing to verify correctness properties across a wide range of inputs:

**Backend (Java):**
- Framework: jqwik (property-based testing for Java)
- Minimum iterations: 100 per property test
- Each property test must reference its corresponding design property using the format: `**Feature: web-calendar, Property {number}: {property_text}**`

**Frontend (JavaScript/TypeScript):**
- Framework: fast-check (property-based testing for JavaScript)
- Minimum iterations: 100 per property test
- Each property test must reference its corresponding design property using the format: `**Feature: web-calendar, Property {number}: {property_text}**`

**Property Test Coverage:**
- Each correctness property listed above must be implemented as a single property-based test
- Tests should generate random valid inputs (dates, note content, etc.)
- Tests should verify the property holds across all generated inputs
- Edge cases (empty strings, boundary dates, etc.) should be included in generators

**Integration Testing:**
- API endpoint integration tests
- Database persistence verification
- Frontend-backend communication
- Speech API integration (with mocked browser API)

### Test Execution Strategy

1. Unit tests run on every build
2. Property-based tests run on every build (100+ iterations each)
3. Integration tests run before deployment
4. Manual testing for speech-to-text functionality across different browsers

## Performance Considerations

- Database indexing on `date` field for fast note retrieval
- Pagination for dates with many notes (if needed in future)
- Lazy loading of notes (only fetch when date is selected)
- Debouncing for speech-to-text input to avoid excessive API calls
- Caching of current month's notes in frontend

## Security Considerations

- **Password Security**: BCrypt hashing with salt for all passwords (never store plaintext)
- **JWT Security**: Tokens signed with secret key, include expiration time (24 hours)
- **Authentication**: Spring Security filter chain validates JWT on protected endpoints
- **Authorization**: User ownership verification before any note modification
- **Input Sanitization**: Prevent XSS attacks through input validation and encoding
- **SQL Injection Prevention**: JPA parameterized queries for all database operations
- **CORS Configuration**: Restrict allowed origins for frontend-backend communication
- **Rate Limiting**: API endpoint rate limiting (future enhancement)
- **Content Length Validation**: Prevent large payload attacks
- **Secure Headers**: HTTPS enforcement, security headers (X-Frame-Options, etc.)

## Deployment Architecture

**Development:**
- Backend: Spring Boot with PostgreSQL database (Docker container)
- Frontend: React development server
- Both running locally on different ports (Backend: 8080, Frontend: 3000)
- PostgreSQL running in Docker on port 5432

**Production:**
- Backend: Spring Boot application deployed as JAR
- Database: PostgreSQL (managed service or dedicated server)
- Frontend: Static files served via Nginx or similar
- HTTPS for secure communication
- Environment-specific JWT secrets and database credentials
