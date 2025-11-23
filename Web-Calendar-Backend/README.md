# Web Calendar Backend

Spring Boot backend application for the Web Calendar system.

## Technology Stack

- Java 17
- Spring Boot 3.2.0
- Spring Security with JWT authentication
- Spring Data JPA
- PostgreSQL
- Gradle 8.5
- jqwik (Property-based testing)

## Project Structure

```
src/
├── main/
│   ├── java/com/webcalendar/
│   │   ├── WebCalendarApplication.java
│   │   ├── entity/          # JPA entities
│   │   ├── repository/      # Spring Data repositories
│   │   ├── service/         # Business logic
│   │   ├── controller/      # REST controllers
│   │   ├── security/        # Security configuration
│   │   └── dto/             # Data Transfer Objects
│   └── resources/
│       └── application.properties
└── test/
    └── java/com/webcalendar/
```

## Prerequisites

- Java 17 or higher
- PostgreSQL 12 or higher
- Gradle 8.5 (included via wrapper)

## Database Setup

### Option 1: Using PostgreSQL CLI

1. Install PostgreSQL 12 or higher
2. Start PostgreSQL service
3. Create the database:
   ```bash
   # On Windows (using psql)
   psql -U postgres
   ```
   Then run:
   ```sql
   CREATE DATABASE webcalendar;
   \q
   ```

### Option 2: Using pgAdmin or other GUI tools

1. Open pgAdmin or your preferred PostgreSQL GUI
2. Connect to your PostgreSQL server
3. Right-click on "Databases" and select "Create" > "Database"
4. Name it `webcalendar`
5. Click "Save"

### Option 3: Using the provided SQL script

Run the `database-setup.sql` script located in the project root:
```bash
psql -U postgres -f database-setup.sql
```

### Configuration

The default configuration in `application.properties` is:
- **Host**: localhost
- **Port**: 5432
- **Database**: webcalendar
- **Username**: postgres
- **Password**: postgres

Update these values in `src/main/resources/application.properties` if your setup differs.

### Schema Creation

The database schema (tables, columns, constraints) will be automatically created by Spring Boot JPA when the application starts. The configuration `spring.jpa.hibernate.ddl-auto=update` ensures that:
- Tables are created if they don't exist
- Existing tables are updated to match entity definitions
- No data is lost during updates

## Running the Application

```bash
# Build the project
./gradlew build

# Run tests
./gradlew test

# Test database connection
./gradlew test --tests DatabaseConnectionTest

# Run the application
./gradlew bootRun
```

The application will start on `http://localhost:8080`

### Verifying Database Connection

After setting up the database, you can verify the connection by running:
```bash
./gradlew test --tests DatabaseConnectionTest
```

This will test:
- Database connection establishment
- JDBC template functionality
- Database schema accessibility

## Configuration

Key configuration properties in `application.properties`:
- `server.port`: Server port (default: 8080)
- `spring.datasource.url`: PostgreSQL connection URL
- `jwt.secret`: JWT signing secret (change in production)
- `jwt.expiration`: JWT token expiration time in milliseconds

## API Endpoints

Authentication endpoints:
- `POST /api/auth/register` - Register new user
- `POST /api/auth/login` - Login and receive JWT token

Note endpoints (require authentication):
- `POST /api/notes` - Create a new note
- `GET /api/notes?date={date}` - Get notes for a specific date
- `PUT /api/notes/{id}` - Update a note
- `DELETE /api/notes/{id}` - Delete a note
