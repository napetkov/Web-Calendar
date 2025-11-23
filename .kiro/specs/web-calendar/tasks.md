# Implementation Plan

- [x] 1. Set up backend project structure with Spring Boot and Gradle





  - Initialize Spring Boot project with Gradle
  - Configure dependencies: Spring Web, Spring Security, Spring Data JPA, PostgreSQL driver, JWT library
  - Set up application.properties for PostgreSQL connection
  - Create package structure: entities, repositories, services, controllers, security, dto
  - _Requirements: 9.1, 9.2, 9.3, 9.4_

- [x] 2. Configure PostgreSQL database connection







  - Configure application.properties with local PostgreSQL connection details
  - Create database schema for the application
  - Test database connection from Spring Boot
  - _Requirements: 6.1, 6.2_

- [ ] 3. Implement User entity and repository
  - Create User entity with email, password, createdAt fields
  - Add JPA annotations and constraints
  - Create UserRepository interface with findByEmail and existsByEmail methods
  - _Requirements: 7.1, 8.1_

- [ ]* 3.1 Write property test for User entity
  - **Property 17: User registration creates account with encrypted password**
  - **Validates: Requirements 7.1**

- [ ] 4. Implement Note entity and repository
  - Create Note entity with user relationship, date, content, timestamps
  - Add JPA annotations and constraints
  - Create NoteRepository with user-scoped query methods
  - _Requirements: 2.2, 6.1, 8.1_

- [ ]* 4.1 Write property test for Note entity
  - **Property 4: Note persistence round-trip**
  - **Validates: Requirements 2.2, 6.1**

- [ ] 5. Implement JWT utility class
  - Create JwtUtil class for token generation and validation
  - Implement methods: generateToken, validateToken, extractEmail
  - Configure JWT secret and expiration in application.properties
  - _Requirements: 7.2_

- [ ]* 5.1 Write unit tests for JWT utility
  - Test token generation with valid user
  - Test token validation with valid/invalid tokens
  - Test email extraction from token
  - _Requirements: 7.2_

- [ ] 6. Implement Spring Security configuration
  - Create SecurityConfig class with JWT filter
  - Configure authentication entry point
  - Set up password encoder (BCrypt)
  - Configure CORS settings
  - Define public endpoints (/api/auth/**) and protected endpoints (/api/notes/**)
  - _Requirements: 7.1, 7.2, 7.3_

- [ ] 7. Implement authentication service and DTOs
  - Create RegisterRequest, LoginRequest, and AuthResponse DTOs
  - Implement AuthService with register and login methods
  - Add password hashing on registration
  - Add credential validation on login
  - _Requirements: 7.1, 7.2, 7.3_

- [ ]* 7.1 Write property test for authentication
  - **Property 18: Valid login credentials authenticate user**
  - **Validates: Requirements 7.2**

- [ ]* 7.2 Write property test for invalid credentials
  - **Property 19: Invalid login credentials are rejected**
  - **Validates: Requirements 7.3**

- [ ] 8. Implement authentication controller
  - Create AuthController with /register and /login endpoints
  - Add request validation
  - Handle duplicate email errors
  - Return JWT token on successful authentication
  - _Requirements: 7.1, 7.2_

- [ ]* 8.1 Write unit tests for authentication endpoints
  - Test successful registration
  - Test duplicate email registration
  - Test successful login
  - Test invalid login
  - _Requirements: 7.1, 7.2, 7.3_

- [ ] 9. Implement note service with user isolation
  - Create NoteService with user-scoped CRUD operations
  - Implement createNote with validation (non-empty, length check)
  - Implement getNotesByDate (user-scoped)
  - Implement updateNote with ownership verification
  - Implement deleteNote with ownership verification
  - _Requirements: 2.2, 2.4, 4.1, 5.2, 5.3, 8.2, 8.3_

- [ ]* 9.1 Write property test for note validation
  - **Property 5: Empty note rejection**
  - **Validates: Requirements 2.4**

- [ ]* 9.2 Write property test for user isolation
  - **Property 20: Authenticated users see only their own notes**
  - **Validates: Requirements 7.4, 8.2**

- [ ]* 9.3 Write property test for note ownership
  - **Property 21: Notes are associated with creating user**
  - **Validates: Requirements 8.1**

- [ ]* 9.4 Write property test for access control
  - **Property 22: Users cannot access other users' notes**
  - **Validates: Requirements 8.3**

- [ ] 10. Implement note controller with authentication
  - Create NoteController with authenticated endpoints
  - Implement POST /api/notes (create note)
  - Implement GET /api/notes?date={date} (get notes for date)
  - Implement PUT /api/notes/{id} (update note with ownership check)
  - Implement DELETE /api/notes/{id} (delete note with ownership check)
  - Extract authenticated user from JWT token
  - _Requirements: 2.2, 4.1, 5.2, 5.3, 9.1, 9.2, 9.3, 9.4_

- [ ]* 10.1 Write property test for note retrieval order
  - **Property 8: Notes retrieved in chronological order**
  - **Validates: Requirements 4.2**

- [ ]* 10.2 Write property test for note deletion
  - **Property 12: Note deletion removes from storage**
  - **Validates: Requirements 5.3, 6.4**

- [ ]* 10.3 Write property test for edit timestamp preservation
  - **Property 11: Edit preserves creation timestamp**
  - **Validates: Requirements 5.2**

- [ ]* 10.4 Write property test for API error handling
  - **Property 24: Invalid API requests return proper error codes**
  - **Validates: Requirements 9.5**

- [ ] 11. Implement global exception handler
  - Create GlobalExceptionHandler with @ControllerAdvice
  - Handle validation errors (400)
  - Handle authentication errors (401)
  - Handle authorization errors (403)
  - Handle resource not found errors (404)
  - Handle duplicate resource errors (409)
  - Return consistent error response format
  - _Requirements: 7.3, 9.5_

- [ ] 12. Checkpoint - Ensure all backend tests pass
  - Ensure all tests pass, ask the user if questions arise.

- [ ] 13. Set up frontend project with React
  - Initialize React project with Create React App or Vite
  - Install dependencies: react-router-dom, axios, date-fns
  - Set up project structure: components, services, utils, contexts
  - Configure API base URL for backend communication
  - _Requirements: 1.1, 1.2_

- [ ] 14. Implement authentication context and service
  - Create AuthContext for managing authentication state
  - Create AuthService for API calls (register, login, logout)
  - Implement JWT token storage in localStorage
  - Add axios interceptor for attaching JWT to requests
  - Handle token expiration and redirect to login
  - _Requirements: 7.2, 7.5_

- [ ] 15. Implement login and registration forms
  - Create LoginForm component with email and password fields
  - Create RegisterForm component with email, password, and confirm password
  - Add form validation (email format, password length, password match)
  - Display error messages from API
  - Redirect to calendar on successful authentication
  - _Requirements: 7.1, 7.2, 7.3_

- [ ]* 15.1 Write unit tests for authentication forms
  - Test form validation
  - Test successful login flow
  - Test error display
  - _Requirements: 7.1, 7.2, 7.3_

- [ ] 16. Implement private route component
  - Create PrivateRoute component for route protection
  - Check for valid JWT token
  - Redirect to login if not authenticated
  - _Requirements: 7.5_

- [ ] 17. Implement calendar view component
  - Create CalendarView component displaying monthly grid
  - Implement month navigation (previous/next)
  - Highlight current date
  - Handle date selection
  - Display visual indicators for dates with notes
  - Add logout button
  - _Requirements: 1.1, 1.2, 1.3, 1.5, 2.5_

- [ ]* 17.1 Write property test for month navigation
  - **Property 1: Month navigation updates calendar view**
  - **Validates: Requirements 1.2**

- [ ]* 17.2 Write unit tests for calendar rendering
  - Test current month display
  - Test date highlighting
  - Test note indicators
  - _Requirements: 1.1, 1.5, 2.5_

- [ ] 18. Implement note service for API communication
  - Create NoteService with axios for API calls
  - Implement createNote, getNotesByDate, updateNote, deleteNote
  - Handle API errors and return appropriate messages
  - _Requirements: 2.2, 4.1, 5.2, 5.3_

- [ ] 19. Implement note list component
  - Create NoteList component to display notes for selected date
  - Show note content and creation timestamp
  - Display empty state when no notes exist
  - Add edit and delete buttons for each note
  - Implement edit mode with inline editing
  - _Requirements: 4.1, 4.2, 4.3, 4.4, 5.1_

- [ ]* 19.1 Write property test for note display
  - **Property 2: Date selection displays associated notes**
  - **Validates: Requirements 1.3, 2.3, 4.1**

- [ ]* 19.2 Write property test for note display content
  - **Property 9: Note display includes content and timestamp**
  - **Validates: Requirements 4.4**

- [ ]* 19.3 Write property test for edit/delete options
  - **Property 10: Existing notes provide edit and delete options**
  - **Validates: Requirements 5.1**

- [ ] 20. Implement note input component
  - Create NoteInput component with text area
  - Add character counter (max 2000 characters)
  - Implement save and cancel buttons
  - Validate non-empty content before save
  - Display validation errors
  - Clear input after successful save
  - _Requirements: 2.1, 2.2, 2.4_

- [ ]* 20.1 Write property test for note input interface
  - **Property 3: Date selection provides note input interface**
  - **Validates: Requirements 2.1**

- [ ]* 20.2 Write unit tests for note input validation
  - Test empty note rejection
  - Test character limit
  - Test successful save
  - _Requirements: 2.2, 2.4_

- [ ] 21. Implement speech-to-text component
  - Create SpeechToText component using Web Speech API
  - Add microphone button to activate/deactivate recording
  - Display real-time transcription
  - Populate note input field with transcribed text
  - Handle browser compatibility (show fallback for unsupported browsers)
  - Handle microphone permission errors
  - Add visual feedback during recording
  - _Requirements: 3.1, 3.2, 3.3, 3.4, 3.5_

- [ ]* 21.1 Write property test for speech transcription
  - **Property 7: Speech transcription populates input**
  - **Validates: Requirements 3.3**

- [ ]* 21.2 Write unit tests for speech-to-text
  - Test microphone activation
  - Test transcription population
  - Test error handling
  - _Requirements: 3.1, 3.3, 3.4_

- [ ] 22. Integrate all components and implement state management
  - Connect CalendarView, NoteList, NoteInput, and SpeechToText components
  - Implement state management for selected date and notes
  - Fetch notes when date is selected
  - Update calendar indicators after note creation/deletion
  - Handle loading states and error messages
  - _Requirements: 1.3, 2.3, 2.5, 5.4_

- [ ]* 22.1 Write property test for calendar update after deletion
  - **Property 13: Deletion updates calendar view**
  - **Validates: Requirements 5.4**

- [ ]* 22.2 Write property test for edit cancellation
  - **Property 14: Edit cancellation restores original content**
  - **Validates: Requirements 5.5**

- [ ] 23. Implement styling and responsive design
  - Add CSS/Tailwind styling for all components
  - Ensure responsive design for mobile and desktop
  - Style calendar grid with proper spacing
  - Style forms with clear validation feedback
  - Add loading spinners and transitions
  - _Requirements: 1.1, 1.4, 1.5_

- [ ] 24. Add application-level error handling
  - Implement error boundary component
  - Add toast notifications for success/error messages
  - Handle network errors gracefully
  - Display user-friendly error messages
  - _Requirements: 3.4, 7.3_

- [ ] 25. Final checkpoint - End-to-end testing
  - Ensure all tests pass, ask the user if questions arise.

- [ ]* 26. Write property test for persistence across restarts
  - **Property 15: Application restart preserves notes**
  - **Validates: Requirements 6.2**

- [ ]* 27. Write property test for immediate persistence
  - **Property 16: Note updates persist immediately**
  - **Validates: Requirements 6.3**

- [ ]* 28. Write property test for user isolation across operations
  - **Property 23: User isolation is maintained across all operations**
  - **Validates: Requirements 8.4**

- [ ]* 29. Write property test for visual indicators
  - **Property 6: Notes display with visual indicators**
  - **Validates: Requirements 2.5**
