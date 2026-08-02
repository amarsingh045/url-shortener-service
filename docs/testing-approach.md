# Testing Approach, Limitations, And Trade-Offs

## Test Strategy
The project uses multiple layers of automated validation.

### 1. Headless acceptance tests
- Implemented with Cucumber and Gherkin
- Run against Spring Boot with MockMvc
- Focus on business-visible behavior that non-developers can read and discuss

### 2. Integration tests
- Existing controller tests validate end-to-end API behavior with the real Spring context
- These tests check status codes, redirects, payloads, and analytics behavior

### 3. Focused unit tests
- Service tests validate business rules, analytics aggregation, fallback behavior, and short-code collision retry behavior
- Mapper tests validate MapStruct conversion between domain and persistence objects

### 4. Coverage enforcement
- JaCoCo is configured in Maven
- The `verify` phase enforces a minimum line coverage ratio of 80%

## What Is Covered Well
- Successful URL shortening
- Redirect behavior
- Not-found behavior
- Invalid URL behavior
- Aggregate analytics behavior
- MapStruct mapping behavior
- Resilience fallback behavior for business exception pass-through and analytics fallback
- Collision retry behavior when duplicate short codes are encountered during persistence

## Limitations
- No browser UI exists, so tests are API-level and headless only
- No load or performance tests are included
- No contract tests against external clients are included
- Logging behavior is exercised indirectly rather than by dedicated log-assertion tests
- Coverage threshold is line-based, not branch-coverage-based

## Trade-Offs
- MockMvc-based tests are fast and deterministic, but they do not validate a real deployed network boundary
- H2 in-memory storage keeps tests simple, but does not simulate production persistence concerns such as schema migrations or database latency
- Cucumber improves readability for non-developers, but step-definition maintenance is higher than plain JUnit tests
- Resilience behavior is configured for prototype clarity; production tuning would require workload-specific testing

## Current Status
- Working prototype: complete
- Architecture overview: documented
- Greenfield, brownfield, and ambiguous scenarios: documented
- Setup instructions: documented
- Testing approach, limitations, and trade-offs: documented
