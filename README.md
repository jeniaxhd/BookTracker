# BookTracker

BookTracker is a multi-module book and reading tracker built with Java and React.  
It includes:
- a core Java module with entities, DAO layer, and JavaFX desktop UI,
- a Spring Boot REST backend,
- and a React web frontend.

This is a student project created together with a classmate.

The application stores data in MySQL (books, authors, genres, users, reading sessions, and reviews).

## Project Structure

- `app/` - core module (entities, DAO, business logic, JavaFX desktop app)
- `server-rest/` - Spring Boot REST API that uses the `app` module
- `web-react/` - React + TypeScript + Vite frontend
- `docker-compose.yml` - local MySQL service definition
- `init.sql` - database schema + seed data
- `test.http`, `server-rest/test*.http` - example HTTP requests

## Tech Stack

- Java 21
- Maven (multi-module build)
- JavaFX 21
- Spring Boot 3.4.2
- Spring JDBC + HikariCP
- MySQL 8.4
- React 19
- TypeScript + Vite

## Prerequisites

Install the following tools:
- JDK 21
- Maven 3.9+
- Docker Desktop
- Node.js 20+ (Node.js 22+ recommended)

## Quick Start

### 1. Start the database

From the project root:

```bash
docker compose up -d
```

Default local DB settings:
- Host: `localhost`
- Port: `3308`
- Database: `booktracker`
- User: `booktracker_user`
- Password: `BookTrackerYevhenVadym2025`

### 2. Run the backend (Spring Boot)

From the project root:

```bash
mvn -pl server-rest spring-boot:run
```

Health check:

```bash
curl http://localhost:8080/api/ping
```

Expected response:

```text
ok
```

### 3. Run the frontend (React)

In a new terminal:

```bash
cd web-react
npm install
npm run dev
```

Frontend URL (default): `http://localhost:5173`

### 4. Run the desktop app (optional)

From the project root:

```bash
mvn -pl app javafx:run
```

## Configuration Notes

- Frontend API base URL: `web-react/src/config.ts` (`http://localhost:8080`)
- Backend CORS is configured for: `http://localhost:5173`
- Database connection properties: `app/src/main/resources/db.properties`

## REST API

Base URL: `http://localhost:8080/api`

### Ping
- `GET /ping`

### Authors
- `GET /authors`
- `GET /authors/{id}`
- `POST /authors`
- `PUT /authors/{id}`
- `DELETE /authors/{id}`

### Books
- `GET /books`
- `GET /books/{id}`
- `POST /books`
- `PUT /books/{id}`
- `DELETE /books/{id}`

### Genres
- `GET /genres`
- `GET /genres/{id}`
- `POST /genres`
- `PUT /genres/{id}`
- `DELETE /genres/{id}`

Note: the current REST layer exposes authors, books, and genres. The database schema includes additional tables used by the broader project domain.

## Useful Commands

Build all Java modules:

```bash
mvn clean install
```

Run Java tests:

```bash
mvn test
```

Build frontend:

```bash
cd web-react
npm run build
```

## Troubleshooting

- Web app cannot reach backend: confirm backend is running on `localhost:8080`.
- Database connection fails: run `docker compose ps` and check MySQL container health.
- Port `3308` is busy: update `docker-compose.yml` and `app/src/main/resources/db.properties`.

## Security

Local database credentials are currently stored in plain text for development convenience.  
For shared/public deployments, move secrets to environment variables or another non-committed secret source.
