# quick-note-api-spring-mysql

![Java](https://img.shields.io/badge/Java-17%2F21-007396?logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-6DB33F?logo=springboot&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-8.x-4479A1?logo=mysql&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-Multi--Stage-2496ED?logo=docker&logoColor=white)

## Project Overview

This repository represents Stack 1 of the Quick-Note Polyglot initiative. It provides a strictly decoupled REST API backend that is designed to serve a separate React frontend over a stable, contract-driven HTTP interface.

The backend is responsible for authentication workflows, note lifecycle management, persistence orchestration, and API-level validation/error handling. The implementation emphasizes maintainability, traceability, and runtime portability for local development and containerized deployment.

## System Architecture and Tech Stack

- Language and Runtime: Java 17/21
- Framework: Spring Boot 3.x ecosystem with layered REST architecture
- Persistence: Spring Data JPA with MySQL
- Build and Dependency Management: Maven Wrapper
- Quality Gate: JaCoCo coverage enforcement in the Maven lifecycle
- Containerization: Multi-stage Docker build for reduced runtime image footprint

Architectural patterns and principles:

- Strict cross-boundary API contract to ensure frontend/backend interoperability
- Separation of concerns across Controller, Service, Repository, DTO, and Entity layers
- Centralized exception handling for deterministic HTTP error semantics
- Contract-first endpoint behavior with explicit validation and stable JSON payloads

## API Contract

The following table summarizes the principal endpoints exposed by this backend.

| Method | Endpoint | Purpose | Request Body | Response |
|---|---|---|---|---|
| POST | /api/auth/login | Authenticate an existing user | { "username": "string", "password": "string" } | { "token": "string", "userId": "string", "username": "string" } |
| POST | /api/auth/register | Register a new user | { "username": "string", "password": "string" } | { "token": "string", "userId": "string", "username": "string" } |
| GET | /api/notes | Retrieve notes for the authenticated user | None (Authorization header required) | Array of Note objects |
| POST | /api/notes | Create a note | { "title": "string", "content": "string" } | Note object |
| PUT | /api/notes/{id} | Update a note | { "title": "string", "content": "string" } | Note object |
| PUT | /api/notes/{id}/pin | Pin or unpin a note | { "isPinned": true/false } | Note object |
| DELETE | /api/notes/{id} | Delete a note | None | { "message": "string", "id": "number" } |

Universal Note payload shape:

{ "id": "string or number", "title": "string", "content": "string", "userId": "string", "createdAt": "ISO-8601 timestamp" }

## Prerequisites

Install the following dependencies on the host machine:

- Java Development Kit (JDK) 17 or 21
- Maven 3.9+ (optional when using Maven Wrapper)
- Docker Engine
- Git

## Local Execution (Step-by-Step)

### 1. Start local MySQL container

Run the following command exactly:

```bash
docker run --name quicknote-mysql -e MYSQL_ROOT_PASSWORD=root -e MYSQL_DATABASE=quicknote_db -p 3307:3306 -d mysql:latest
```

### 2. Run the API locally

From the repository root, start the application:

```bash
./mvnw spring-boot:run
```

On Windows Command Prompt:

```bat
mvnw.cmd spring-boot:run
```

The API starts on port 8080 by default unless overridden via application configuration.

### 3. Inspect local MySQL tables and contents

If you want to verify the database schema or inspect stored records from Windows Command Prompt, connect to the local MySQL container with the following command:

```bat
mysql -h 127.0.0.1 -P 3307 -u root -p
```

After entering the password, use the application database and query its tables:

```sql
USE quicknote_db;
SHOW TABLES;
SELECT * FROM users;
SELECT * FROM notes;
```

This is the recommended approach for confirming that the users and notes tables were created correctly, and for validating the data persisted by the backend during local development.

## Testing and Code Coverage

Execute the test suite:

```bash
./mvnw clean test
```

On Windows Command Prompt:

```bat
mvnw.cmd clean test
```

Coverage model:

- JaCoCo is integrated into the Maven lifecycle.
- A minimum baseline coverage threshold of 80% is enforced as a project quality standard for critical layers.
- The repository may be configured with stricter thresholds depending on branch policy and quality gate evolution.

## Containerization

This project includes a multi-stage Dockerfile that separates build-time concerns from runtime delivery.

### 1. Build the Docker image

```bash
docker build -t quick-note-api-spring-mysql:local .
```

### 2. Run the Docker container

```bash
docker run --name quick-note-api -p 8080:8080 \
  -e DB_USERNAME=root \
  -e DB_PASSWORD=root \
  quick-note-api-spring-mysql:local
```

On Windows Command Prompt (single line):

```bat
docker run --name quick-note-api -p 8080:8080 -e DB_USERNAME=root -e DB_PASSWORD=root quick-note-api-spring-mysql:local
```

## Operational Notes

- Ensure the backend and frontend remain contract-compatible whenever endpoint or payload changes are proposed.
- Prefer schema-safe migrations and explicit DTO evolution for backward compatibility.
- Keep authentication semantics deterministic: login validates existing users, while registration handles user creation.
