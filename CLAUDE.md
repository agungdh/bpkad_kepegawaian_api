# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Spring Boot 4.0.2 application for employee management (kepegawaian) at BPKAD. Uses Java 25, PostgreSQL with Flyway migrations, Redis/Valkey for caching, and JWT-based authentication.

## Development Commands

### Build & Run
```bash
# Build the project
mvn clean package

# Run the application (requires PostgreSQL and Valkey/Redis)
mvn spring-boot:run

# Start infrastructure services
docker-compose up -d
```

### Database Migrations
```bash
# Migrate database with Flyway
mvn flyway:migrate

# Info on migrations
mvn flyway:info
```

### Testing
```bash
# Run all tests
mvn test

# Run a single test class
mvn test -Dtest=BpkadKepegawaianApplicationTests
```

### Native Image
```bash
# Build GraalVM native image
mvn -Pnative native:compile
```

## Architecture

### Entity & Repository Pattern
- All entities extend `BaseEntity` (id, uuid, timestamps, soft delete fields, audit fields)
- Repositories extend `BaseRepository` which provides soft delete functionality (`findByUuidAndDeletedAtIsNull`, `softDelete`)
- Use UUIDs for public APIs, Long IDs internally
- Soft delete pattern: set `deletedAt` and `deletedBy` instead of hard delete

### Cursor-Based Pagination
- Custom `CursorPaginationRepository` uses JPA Criteria API for cursor pagination
- Services use `fetchCursor()` with entity class, cursor UUID, limit, and sort field
- Cursor is Base64-encoded UUID of the last entity from previous page
- Each entity type has its own `SortableField` enum (e.g., `PegawaiSortableField`)

### Authentication
- JWT-based stateless authentication via `TokenAuthenticationFilter`
- Tokens stored in Redis with configurable TTL (default 24 hours)
- Custom `UserAuthentication` holds `UserData` (username, roles)
- White-listed endpoints: `/api/auth/**`, Swagger UI, Actuator

### Mapping Layer
- MapStruct for entity-to-DTO mapping
- Mappers in `mapper/` package (e.g., `PegawaiMapper`, `BidangMapper`)

### Relationship Loading
- Services manually load relationships (e.g., `loadRelationships()` in `PegawaiService`)
- Entities use foreign key IDs (e.g., `skpdId`, `bidangId`) with lazy-loaded reference objects

## Infrastructure

- PostgreSQL 18 on port 5432 (database: `bpkad_kepegawaian`)
- Valkey (Redis-compatible) on port 6379
- Adminer on port 8083 for database management
- Swagger UI available at `/swagger-ui.html`

## Key Configuration

- Virtual threads enabled (Tomcat max threads = 1)
- Hibernate DDL auto: `validate` (schema managed by Flyway)
- Annotation processors: Lombok, MapStruct, Spring Boot Configuration Processor
