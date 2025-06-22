# Backend Kotlin Spring Boot - Notes Application

This project is a backend for a CRUD Notes application, developed using **Kotlin** and **Spring Boot**. It follows a standard MVC architecture, featuring controllers, services, repositories, and models. The application is secured with JWT-based authentication and user session management, and uses **MongoDB** as its database.

## Features
- User registration with email/password validation
- Secure login returning JWT tokens
- Refresh token mechanism for session maintenance
- Password hashing for security
- JWT-based authentication and session management
- CRUD operations for notes
- Modular MVC structure
- Global validation handling
- Ready for integration with databases and security modules

## API Endpoints

### Auth Endpoints
- `POST /auth/register` — Register a new user (with email/password validation)
- `POST /auth/login` — Login and receive JWT access and refresh tokens
- `POST /auth/refresh` — Refresh access token using a valid refresh token

### Notes Endpoints
- `GET /notes` — Retrieve all notes for the authenticated user
- `POST /notes` — Create a new note
- `DELETE /notes/{id}` — Delete a note by ID

## Token Expiry
- **Access Token:** Valid for 15 minutes
- **Refresh Token:** Valid for 15 days

If the refresh token expires, MongoDB will automatically generate and update the refresh token. Access tokens can be regenerated using a valid refresh token.

## Project Structure
```
├── build.gradle.kts           # Gradle build script (Kotlin DSL)
├── src/
│   ├── main/
│   │   ├── kotlin/            # Application source code
│   │   │   └── com/tandon_ksb/backend_kotlin_springboot/
│   │   │       ├── controller/
│   │   │       ├── database/
│   │   │       ├── security/
│   │   │       ├── BackendKotlinSpringbootApplication.kt
│   │   │       └── GlobalValidationHandler.kt
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       └── kotlin/            # Test source code
│           └── com/tandon_ksb/backend_kotlin_springboot/
│               └── BackendKotlinSpringbootApplicationTests.kt
├── gradle/                    # Gradle wrapper files
├── build/                     # Build output (generated)
└── ...
```

## Getting Started

### Prerequisites
- [JDK 17+](https://adoptopenjdk.net/)
- [Gradle](https://gradle.org/) (or use the included wrapper)
- [MongoDB Atlas](https://www.mongodb.com/atlas) or local MongoDB instance

### Environment Variables
Create a `.env` file or set the following environment variables:

```
MONGODB_CONNECTION_STRING=mongodb+srv://<username>:<pswd>@<project_name>.2fqeq6f.mongodb.net/notes?retryWrites=true&w=majority&appName=tandonExpCKO&tls=true&socketTimeoutMS=6000000&connectTimeoutMS=3000000&tlsAllowInvalidCertificates=true&tlsAllowInvalidHostnames=true

JWT_SECRET_BASE64=b3F5ZHV0ZXI3ODIzeXI4OTM2N3J0MjM4ZGVyeTg5MjN5ZTc4MmY=
```

- Replace `<uname>` and `<pswd>` with your MongoDB Atlas credentials.

### Build & Run

```bash
./gradlew build
./gradlew bootRun
```

The application will start on [http://localhost:8080](http://localhost:8080) by default.

## Configuration
Edit `src/main/resources/application.properties` to configure application settings.

## Testing
You can test the API endpoints using [Postman](https://www.postman.com/). Import the API collection and use the provided endpoints for authentication and notes management.

Run tests with:
```bash
./gradlew test
```

## License
This project is licensed under the MIT License.
