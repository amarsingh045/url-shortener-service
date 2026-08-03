# URL Shortener Service

## Overview

This project is a runnable URL shortener prototype built with Spring Boot. It supports:

- POST `/api/shorten` to create a short URL
- GET `/api/{shortCode}` to redirect to the original URL
- GET `/api/analytics` to return aggregate link analytics

## Quick Start

### Run locally on Windows

```powershell
./mvnw.cmd spring-boot:run
```

### Run tests

```powershell
./mvnw.cmd test
```

### Run verification with coverage gate

```powershell
./mvnw.cmd verify
```

## Example

```bash
curl -X POST http://localhost:8080/api/shorten \
  -H "Content-Type: application/json" \
  -d '{"longUrl":"https://example.com/products"}'
```

## Deliverable Documents

- Primary architecture diagram: `docs/Url_Shortener_Service_Architecture_Diagram.jpg`
- `docs/architecture-overview.md`
- `docs/delivery-scenarios.md`
- `docs/setup-instructions.md`
- `docs/testing-approach.md`

## Notes

- The controller depends on the application-layer use-case contract `UrlShortenerUseCase`
- Business orchestration is implemented in `com.schwab.application.UrlShortenerService`
- Domain keeps core model and repository port abstractions, while infrastructure provides adapter implementations
- The service uses H2 in-memory storage for prototype simplicity
- The persistence adapter uses an in-memory `ConcurrentHashMap` cache for short-code lookups
- Mapping between domain and persistence objects is handled by MapStruct
- Acceptance tests are written in Gherkin and run headlessly using Cucumber + MockMvc
- Short-code collisions are handled with a database-backed uniqueness check plus bounded retry during URL creation

## Production Considerations

- Replace H2 in-memory storage with a production-grade SQL or NoSQL database (for example PostgreSQL, MySQL, or DynamoDB)
- Replace the local in-memory cache with a distributed cache (for example Redis or ElastiCache)
- Keep DB uniqueness constraints for `shortCode` and retain bounded retry logic for collision recovery
