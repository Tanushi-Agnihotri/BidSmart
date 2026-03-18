# BidSmart Backend

Spring Boot backend for the BidSmart auction platform.

Current scope:
- PostgreSQL connection
- Flyway database migrations
- Spring Security base setup
- Buyer/Seller signup API

## Tech Stack

- Java 21
- Spring Boot 4
- Spring Web MVC
- Spring Data JPA
- Spring Security
- PostgreSQL
- Flyway

## Prerequisites

- Java 21
- Docker Desktop or a local PostgreSQL instance

## Project Status

Implemented right now:
- PostgreSQL datasource wiring
- Docker Compose setup for local Postgres
- `users` table migration
- `POST /api/auth/signup`

Not implemented yet:
- Login
- JWT authentication
- Auctions APIs
- Bidding APIs
- Watchlist APIs
- Admin APIs

## Local Database Setup

This project includes a local PostgreSQL container:

`docker-compose.yml`

Start the database:

```bash
cd "BidSmart Backend"
docker compose up -d
```

Default database settings:

- Database: `bidsmart_db`
- Username: `bidsmart_user`
- Password: `bidsmart_password`
- Port: `5432`

## Application Configuration

Main backend config lives in:

`src/main/resources/application.properties`

Supported environment variables:

- `DB_URL`
- `DB_USERNAME`
- `DB_PASSWORD`
- `SERVER_PORT`
- `FRONTEND_URL`

Default local values:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/bidsmart_db
spring.datasource.username=bidsmart_user
spring.datasource.password=bidsmart_password
server.port=8081
app.cors.allowed-origin=http://localhost:8080
```

## Run the Backend

Start the app with the Maven wrapper:

```bash
cd "BidSmart Backend"
./mvnw spring-boot:run
```

Compile check:

```bash
./mvnw -DskipTests compile
```

## How to Run the Server

Follow these steps in order:

### 1. Start PostgreSQL

```bash
cd "BidSmart Backend"
docker compose up -d
```

### 2. Start the Spring Boot server

```bash
cd "BidSmart Backend"
./mvnw spring-boot:run
```

### 3. Confirm the server is running

Backend base URL:

```text
http://localhost:8081
```

Signup endpoint:

```text
http://localhost:8081/api/auth/signup
```

### 4. Stop the server

Press `Ctrl + C` in the terminal where Spring Boot is running.

### 5. Stop PostgreSQL

```bash
cd "BidSmart Backend"
docker compose down
```

### Run with custom environment variables

If you want to run the server with custom DB credentials or a different port:

```bash
DB_URL=jdbc:postgresql://localhost:5432/bidsmart_db \
DB_USERNAME=bidsmart_user \
DB_PASSWORD=bidsmart_password \
SERVER_PORT=8081 \
FRONTEND_URL=http://localhost:8080 \
./mvnw spring-boot:run
```

## Database Migration

Flyway is enabled, and the first migration creates the `users` table:

`src/main/resources/db/migration/V1__create_users.sql`

The backend uses:

- `spring.jpa.hibernate.ddl-auto=validate`
- Flyway for schema creation

That means:
- Flyway creates the tables
- Hibernate validates them
- JPA does not auto-create schema

## Signup API

Endpoint:

```http
POST /api/auth/signup
```

Controller:

`src/main/java/com/example/BidSmart/auth/AuthController.java`

Service logic:

`src/main/java/com/example/BidSmart/auth/AuthService.java`

### Request Body

```json
{
  "fullName": "Tanushi Agnihotri",
  "email": "tanushi@example.com",
  "password": "StrongPass123",
  "role": "buyer"
}
```

Accepted roles:
- `buyer`
- `seller`

Notes:
- `admin` signup is blocked
- email is normalized to lowercase
- password is hashed with BCrypt
- duplicate email returns `409 Conflict`

### Success Response

```json
{
  "id": "uuid",
  "fullName": "Tanushi Agnihotri",
  "email": "tanushi@example.com",
  "role": "BUYER",
  "status": "ACTIVE",
  "createdAt": "2026-03-13T00:00:00Z"
}
```

### Example cURL

```bash
curl -X POST http://localhost:8081/api/auth/signup \
  -H "Content-Type: application/json" \
  -d '{
    "fullName": "Tanushi Agnihotri",
    "email": "tanushi@example.com",
    "password": "StrongPass123",
    "role": "buyer"
  }'
```

## Frontend Integration

The frontend signup page is already connected to this endpoint.

Frontend API helper:

`../BidSmart Frontend/src/lib/api.ts`

Frontend signup page:

`../BidSmart Frontend/src/pages/Register.tsx`

If your backend runs on a different port or host, set:

```bash
VITE_API_BASE_URL=http://localhost:8081
```

## Project Structure

```text
src/main/java/com/example/BidSmart
|- auth
|- config
|- exception
|- user

src/main/resources
|- application.properties
|- db/migration
```

## Common Issues

### 1. Database connection failed

Check:
- Postgres container is running
- port `5432` is free
- DB credentials match your env vars

### 2. Flyway migration errors

Check:
- database is reachable
- migration file names follow `V__description.sql` format
- existing schema was not manually changed in a conflicting way

### 3. Frontend signup gives CORS error

Set:

```bash
FRONTEND_URL=http://localhost:8080
```

and restart the backend.

## Next Steps

Recommended next backend tasks:

1. Implement login API
2. Add JWT token generation and auth filter
3. Add `GET /api/auth/me`
4. Connect frontend login page
5. Start auction module
