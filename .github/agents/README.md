# Agentic Workflow for the URL Shortener Service

This folder contains markdown-based agent definitions for an agentic development workflow for the URL shortener service.

## Goal

These agents work in sequence to turn a requirement into a reviewed and tested implementation. Each agent must produce a structured handoff package and pause for human approval before sending work to the next agent.

## Agent Personas

- **Planner Agent**: A strategic systems thinker who turns ambiguity into a clear plan.
- **Challenger Agent**: A skeptical reviewer who questions assumptions and exposes risks.
- **Implementer Agent**: A practical builder who turns approved decisions into working code.
- **Reviewer Agent**: A disciplined quality guardian who checks correctness and maintainability.
- **Test Agent**: A behavior-focused validator who ensures the solution is covered by clear acceptance tests.

## Architecture Principles

All design and implementation work should follow Domain-Driven Design (DDD) and Clean Architecture:

- The domain layer owns business rules and core concepts such as `ShortUrl`, `ShortCode`, and redirect behavior.
- The application layer contains use cases and orchestration without leaking infrastructure concerns.
- The infrastructure layer handles persistence, HTTP, and external integrations.
- The interface layer exposes REST endpoints and DTOs.
- Dependencies should point inward; the domain should not depend on frameworks or databases.

## Agent Sequence

1. Planner Agent
   - Turns product requirements into an execution plan.
2. Challenger Agent
   - Reviews the plan for weaknesses, edge cases, risks, and missing constraints.
3. Implementer Agent
   - Implements the approved plan and addresses the challenger feedback.
4. Reviewer Agent
   - Reviews the implementation for correctness, quality, and alignment.
5. Test Agent
   - Writes and organizes Cucumber acceptance tests for the requirement.

## Human Approval Gate

No agent should hand off work to the next agent until the human approver has confirmed the current step.

When a handoff is ready, the current agent should present:

- Summary of what was completed
- Key decisions made
- Open questions
- Risks or assumptions
- Suggested next action
- A clear approval request for the human reviewer

## Shared Handoff Template

Use this structure for every handoff:

- `from_agent`: Name of the current agent
- `to_agent`: Name of the next agent
- `status`: `ready_for_review`, `blocked`, or `approved`
- `requirement_summary`: One-paragraph summary of the requirement
- `work_completed`: Bullet list of completed work
- `decisions`: Key product or technical decisions
- `risks_and_assumptions`: Risks, open questions, and assumptions
- `approval_request`: Clear ask for the human approver
- `next_action`: What the next agent should do
