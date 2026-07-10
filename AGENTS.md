# AGENTS.md

This file provides guidance to Codex (Codex.ai/code) when working with code in this repository.

## 작업 규칙

- 모든 설명은 한국어로
- push는 절대 하지 않을 것

## Project Overview

Rentrella — a rental umbrella-stand service backend. The API spec (endpoints, request/response bodies) is maintained in the Notion workspace under the "렌트렐라" page → "api 명세서" database; check it before implementing a controller's business logic.

## Tech Stack

- **Java 21** with **Lombok** (use `@Getter`, `@Setter`, `@Builder`, `@RequiredArgsConstructor`, etc. to reduce boilerplate)
- **Spring Boot 4.1.0** — use Spring's standard auto-configuration
- **Spring MVC** (`spring-boot-starter-webmvc`) — REST controllers
- **Spring Data JPA** + **MySQL** — persistence layer
- **Spring Security** — dependency present but currently unconfigured
- **spring-dotenv** — loads `.env` at the project root into Spring properties (see below)

## Commands

```bash
./gradlew build              # full build (compiles + runs tests)
./gradlew bootRun            # run the application locally
./gradlew test               # run all tests
./gradlew test --tests com.example.rentrella.RentrellaApplicationTests  # run a single test class
./gradlew test --tests "*.RentrellaApplicationTests.contextLoads"       # run a single test method
```

## Environment / DB configuration

`application.properties` reads DB credentials from environment variables (`${DB_URL}`, `${DB_USERNAME}`, `${DB_PASSWORD}`), which are supplied via a `.env` file at the project root (auto-loaded by `spring-dotenv`, not committed to git). Copy `.env.example` to `.env` and fill in real values before running the app or any test that boots the Spring context (e.g. `RentrellaApplicationTests`).

## Package Structure Convention

Root package: `com.example.rentrella`

Packages are organized **by feature/domain, not by technical layer** — there is no shared top-level `controller`/`service`/`repository` package. Each feature gets its own package containing whatever layers it needs (currently just controllers; add `service`, `repository`, `domain`/`entity`, `dto` classes inside the same feature package as the feature grows):

- `auth` — login/logout, signup, token reissue, email verification code, password reset
- `umbrella` — checking availability, renting, returning, viewing one's own rented umbrella
- `profile` — 마이페이지 (user stats/settings)
- `inquiry` — user-submitted inquiries (creation)
- `alarm` — notifications
- `admin` — admin-only endpoints: inquiry review/completion, rental logs, locking a slot/user, opening a slot
- `device` — IoT umbrella-stand device polling and command result reporting

Cross-cutting concerns (e.g. Spring Security config) belong in a `config` package when added.

Controllers currently in the repo are stubs — each endpoint method throws `UnsupportedOperationException()` as a placeholder until the corresponding service logic is implemented against the Notion API spec.
