# Setup Instructions

## Prerequisites
- Java 17 installed and available on `PATH`
- Maven wrapper available in the repository
- A terminal on Windows, macOS, or Linux

## Clone Or Open The Project
Open the repository folder in VS Code or any IDE that supports Maven and Spring Boot.

## Run The Application
### Windows
```powershell
./mvnw.cmd spring-boot:run
```

### macOS or Linux
```bash
./mvnw spring-boot:run
```

The application starts on `http://localhost:8080`.

## Useful Endpoints
- `POST /api/shorten`
- `GET /api/{shortCode}`
- `GET /api/analytics`
- `GET /v3/api-docs`
- `GET /actuator/health`
- `GET /actuator/prometheus`
- H2 console: `http://localhost:8080/h2-console`

## Example Requests
### Create a short URL
```bash
curl -X POST http://localhost:8080/api/shorten \
  -H "Content-Type: application/json" \
  -d '{"longUrl":"https://example.com/products"}'
```

### Resolve a short code
```bash
curl -i http://localhost:8080/api/abc123
```

### Read analytics
```bash
curl http://localhost:8080/api/analytics
```

## Run Automated Tests
### Run all tests
```powershell
./mvnw.cmd test
```

### Run full verification with coverage gate
```powershell
./mvnw.cmd verify
```

## Read The Cucumber Scenarios
The acceptance scenarios are stored in:
- `src/test/resources/features/url_shortener.feature`

These tests run headlessly through MockMvc and do not require a browser.

## Notes
- The database is H2 in-memory, so data resets when the application restarts
- The project uses Spring Boot auto-configuration, so no extra setup is required for local execution
- Short codes are protected by a database uniqueness constraint, and the service retries code generation up to 5 times if a collision occurs during insert

## Optional OpenTelemetry Tracing
Tracing is wired into the application but disabled by default so local runs do not fail when no OTLP collector is running.

To enable tracing, provide both of these environment variables before starting the app:

### Windows PowerShell
```powershell
$env:TRACING_ENABLED="true"
$env:OTEL_EXPORTER_OTLP_TRACES_ENDPOINT="http://localhost:4318/v1/traces"
./mvnw.cmd spring-boot:run
```

### Notes
- Start an OpenTelemetry Collector or another OTLP-compatible backend first
- When tracing is enabled, log lines include `traceId` and `spanId` for correlation
- Prometheus remains metrics-only and does not store logs
