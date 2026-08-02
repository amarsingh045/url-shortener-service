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

- The service uses H2 in-memory storage for prototype simplicity
- Mapping between domain and persistence objects is handled by MapStruct
- Acceptance tests are written in Gherkin and run headlessly using Cucumber + MockMvc
- Short-code collisions are handled with a database-backed uniqueness check plus bounded retry during URL creation
