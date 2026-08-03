# Delivery Scenarios

This document maps the prototype approach to three delivery situations the requirement asked for: greenfield, brownfield, and ambiguous delivery.

## 1. Greenfield Scenario
### Situation
A new URL shortener service must be created from scratch with no existing codebase.

### Decomposition
1. Define API endpoints and payloads
2. Implement controller and service contract
3. Implement business logic for shorten, resolve, and analytics
4. Add persistence and object mapping
5. Add error handling, observability, and resilience
6. Add executable tests and coverage checks

### Execution In This Prototype
- New controller, service, domain models, repository abstractions, and persistence adapter were created
- H2 was chosen to keep the prototype runnable without external infrastructure
- MapStruct was introduced to reduce manual mapping code
- Cucumber scenarios were added so the behavior is readable by non-developers
- Short-code collision handling was implemented with database uniqueness enforcement and retry-based regeneration

### Validation
- End-to-end HTTP tests pass
- Headless Cucumber scenarios pass
- JaCoCo coverage gate is configured at 90%

## 2. Brownfield Scenario
### Situation
An existing codebase already has overlapping implementations or inconsistent layers and needs cleanup while preserving behavior.

### Decomposition
1. Identify the real runtime path and unused classes
2. Consolidate duplicate implementations
3. Rename or move classes to align package structure with actual responsibilities
4. Verify wiring, mapping, resilience, and tests after the refactor

### Execution In This Prototype
- The active runtime path is now represented by the application-layer `UrlShortenerUseCase`
- Unused duplicate service implementations were removed
- The resilience-oriented implementation is placed in the `application` package as `UrlShortenerService`
- Repository mapping was switched from manual conversion to MapStruct-backed conversion
- Collision handling was hardened so duplicate short-code inserts are retried instead of silently depending only on a pre-check

### Validation
- Application compiles after refactor
- Existing integration tests still pass
- New Cucumber tests cover user-visible behavior after cleanup

## 3. Ambiguous Scenario
### Situation
Requirements are partially defined, or the intent is clear but the implementation details are not.

### Decomposition
1. Build the smallest runnable slice first
2. make design choices explicit in docs
3. keep architecture replaceable where assumptions may change
4. add tests around visible behavior before optimizing internals

### Execution In This Prototype
- The project started as a working slice with create and resolve behavior
- Storage, resilience, mapping, and analytics were added incrementally
- Ports and adapters were used so persistence and service details can evolve later
- Business-exception handling was clarified after observing resilience behavior in tests

### Validation
- Behavior is captured in human-readable Gherkin scenarios
- Key assumptions and trade-offs are documented
- Coverage and automated tests give regression protection while the design evolves
