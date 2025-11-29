# Budget OK Backend - Project Summary

## Overview
Budget OK is a Spring Boot REST API backend for a personal budgeting application using the "envelope budgeting" method. Users can create envelopes (budget categories), track expenses, and transfer funds between envelopes.

## Core Features
- **Envelope Management**: Create, read, update, delete budget envelopes with allocated budgets
- **Expense Tracking**: Add withdrawals and deposits to envelopes, dynamic balance calculation
- **Fund Transfers**: Transfer money between envelopes with validation
- **External API Integration**: Integration with banking APIs (dummyjson.com)
- **User Security**: Spring Security with BCrypt password encoding, CORS support

## Technology Stack
- **Framework**: Spring Boot 3.5.6, Java 17
- **Database**: PostgreSQL 15 + Spring Data JDBC
- **Schema Management**: Liquibase
- **Security**: Spring Security, BCrypt
- **Testing**: JUnit 5, Testcontainers, Pact contracts
- **Build**: Maven, Docker, GitHub Actions CI/CD

## Architecture
Layered architecture with clear separation:
```
Controllers (REST & Web) → Services (Business Logic) → Repositories → Database
                              ↑
                          Security Layer
```

## Key Components

### Application Layer (`application/`)
- `EnvelopeService/EnvelopeServiceImpl`: Business logic for envelope operations
- `Envelope`, `Expense`: Domain entities
- `ExpenseDto`, `TransferRequest`, `TransferResponse`: DTOs for API contracts

### Infrastructure Layer (`infrastructure/`)
- `EnvelopeRepository`: Repository abstraction with JDBC implementation
- `SecurityConfig`: Spring Security configuration
- `JdbcEnvelopeRepository`: Spring Data JDBC interface

### Presentation Layer (`presentation/`)
- `EnvelopeController`: REST API for envelope operations
- `BankOkApiController`: External banking API integration
- `EchoApiController`: Health check endpoint
- `AuthController`, `HomeController`: Auth and web endpoints

## API Endpoints
```
POST   /api/envelopes                  - Create envelope
GET    /api/envelopes                  - Get all envelopes
GET    /api/envelopes/{id}             - Get envelope details
PUT    /api/envelopes/{id}             - Update envelope
PATCH  /api/envelopes/{id}             - Partial update
DELETE /api/envelopes/{id}             - Delete envelope
POST   /api/envelopes/{id}/expenses    - Add expense
POST   /api/envelopes/transfer         - Transfer between envelopes
GET    /api/bankok/carts/{id}          - Fetch external API data
```

## Testing Strategy
- **Unit Tests**: Service logic with in-memory repository
- **Narrow Tests**: Repository/JDBC tests with Testcontainers
- **Component Tests**: Full integration tests with TestRestTemplate
- **Contract Tests**: Pact-based consumer-driven contracts

Test infrastructure uses Testcontainers for PostgreSQL and Kafka.

## Configuration
- **Default Profile**: Local PostgreSQL at `localhost:5432`
- **Production**: Environment-based configuration via env vars
- **CORS**: Enabled for `http://localhost:5173` (frontend)
- **Database Pool**: 2-10 connections (local), 5-15 (production)

## Database Schema
- **envelopes**: id, name, budget
- **envelope_items**: id, amount, memo, transaction_type (WITHDRAW/DEPOSIT), envelope_id

Balance = budget - withdrawals + deposits (calculated dynamically)

## Build & Deployment
- Maven build with Spring Boot Maven Plugin
- Docker containerization
- GitHub Actions CI/CD: Auto-builds and publishes to GitHub Container Registry on push to main
- Workflow file: `.github/workflows/commit-stage-backend.yml`

## Key Design Patterns
- **Service Layer**: Business logic isolation
- **Repository Pattern**: Data access abstraction
- **DTO Pattern**: Data transfer objects for API contracts
- **Builder Pattern**: Lombok annotations for entity construction
- **Dependency Injection**: Constructor injection via Spring
- **Transaction Management**: @Transactional for ACID operations

## Project Structure
```
budget-ok-backend/
├── backend/
│   ├── src/main/java/com/ognjen/budgetok/
│   │   ├── application/          (Services, entities, DTOs)
│   │   ├── infrastructure/       (Repositories, security, persistence)
│   │   └── presentation/         (Controllers)
│   ├── src/main/resources/
│   │   ├── application.yml       (Configuration)
│   │   ├── db/changelog/         (Liquibase migrations)
│   │   └── templates/            (Thymeleaf HTML views)
│   ├── src/test/                 (Comprehensive test suite)
│   ├── pom.xml                   (Maven dependencies)
│   └── Dockerfile                (Container configuration)
├── .github/workflows/            (CI/CD pipelines)
└── README.md
```

## Recent Changes
- Added Jakarta Bean Validation for envelope creation (@NotBlank, @Min)
- Enhanced GlobalExceptionHandler to handle validation errors (MethodArgumentNotValidException)
- Added spring-boot-starter-validation dependency
- Reorganized project structure: moved all code to `backend/` directory
- Added new models: `BankOkCart`, `BankOkProduct`, `Expense` entity
- Enhanced controllers: `BankOkApiController`, `EchoApiController`
- Added Dockerfile and GitHub Actions CI/CD workflow
- Refactored tests with improved structure and contract testing
- Updated application to `BudgetOkBackendApplication`

## Status
- Version: 0.0.1-SNAPSHOT
- Branch: main
- Active development with focus on service layer and testing improvements
