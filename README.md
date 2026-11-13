# The Verve-Guard Fraud Detection Sidecar

This repository contains the materials and implementation for the Interswitch SpringBoot Switch School assessment, featuring the **Securing the Payment Status API** Hands-on Lab and the **Verve-Guard Fraud Detection Sidecar** Capstone Project.

---

## Prerequisites & Setup
- **Java Development Kit (JDK):** Version 17 or higher
- **IDE:** IntelliJ IDEA (recommended) or any modern Java IDE
- **Initializer:** [Spring Initializr](https://start.spring.io)
- **Testing Tools:** Postman or Insomnia
- **Core Dependencies:**
  - Spring Security
  - OAuth2 Resource Server
  - Nimbus JOSE + JWT
  - Spring Data JPA
  - Flyway Migration
  - Spring AOP

---

## 1. Hands-On Lab: Securing the Payment Status API
**Objective:** Implement stateless JSON Web Token (JWT) authentication to protect sensitive transaction status endpoints.

### Core Tasks
1. **Dependency Configuration:** Add `spring-boot-starter-security` and `oauth2-resource-server` dependencies to the project.
2. **Security Configuration:** Create a `SecurityFilterChain` bean:
   - Allow public access to `/auth/**` (e.g., login, registration).
   - Require authentication for all `/api/**` endpoints.
3. **Token Management:** Build a token generator service that signs JWTs with a 15-minute expiration window.
4. **Authentication Endpoint:** Implement a `POST /auth/login` endpoint that authenticates credentials and returns the signed JWT.
5. **Resource Server Configuration:** Configure the resource server to validate Bearer tokens on incoming requests.

### Extra Challenges (For the True OGs)
- **Role-Based Access Control (RBAC):** Include a `roles` claim (e.g., `ROLE_USER`, `ROLE_ADMIN`) in the token and enforce it using Spring Security's `@PreAuthorize` annotation.
- **Custom Exception Handling:** Create a custom `AuthenticationEntryPoint` to return unauthorized responses in the following format:
  ```json
  {
    "code": "99",
    "message": "Unauthorized Access"
  }
  ```

### Verification Scenarios
- `GET /api/status/{id}` without a token $\rightarrow$ `401 Unauthorized`
- `POST /auth/login` with valid credentials $\rightarrow$ Returns signed JWT
- `GET /api/status/{id}` with a valid Bearer token $\rightarrow$ `200 OK`

---

## 2. Capstone Project: The Verve-Guard Fraud Detection Sidecar
**Scenario:** Interswitch requires a high-performance 'sidecar' service that intercepts transaction requests in real-time. This service verifies whether a transaction appears suspicious before allowing it to proceed to the core switch.

### Key Features
- **Real-Time Fraud Ingestion API:** High-throughput endpoint to ingest and evaluate transaction requests.
- **Hybrid Persistence:**
  - **JPA:** For managing and querying blacklisted merchants.
  - **JDBC:** Direct JDBC operations for low-latency logging of transaction requests.
- **In-Memory Rate Limiting:** A custom component to flag IPs exceeding a rate threshold.
- **JWT-Secured Admin Dashboard:** Secure management endpoint to review flagged transaction attempts.
- **Performance Monitoring:** Aspect-Oriented Programming (AOP) / Spring Interceptors to measure audit performance overhead.

### Technical Requirements
1. **Ingestion API:** REST endpoint accepting:
   - Card Number
   - Amount
   - Merchant ID
   - IP Address
2. **Hybrid Storage Strategy:**
   - Use **Spring Data JPA** to manage the merchant blacklist database.
   - Use **Spring JDBC** (`JdbcTemplate`) for high-speed writes to transaction log tables.
3. **Rate Limiting:** Build an in-memory Spring Bean to flag any IP address initiating $>5$ requests per minute.
4. **Admin Panel:** Secure `GET` endpoints with JWT tokens to view log logs of suspicious/flagged transactions.
5. **Auditing & Monitoring:** Implement AOP aspects or interceptors to measure and log execution times.

### Constraints & Deliverables
- **Database Migrations:** Schema creation and setup must be done using **Flyway**.
- **Testing:** Maintain a minimum of **$>80\%$ JUnit test coverage**.
- **Load Testing:** Provide a test script (using a loop, JMeter, or Postman runner) to showcase performance under load.

### Bonus Targets
- Implement a custom Spring Boot **Actuator Health Indicator**.
- Add a **caching layer** to speed up blacklist lookups and measure the performance gains.
- Target latency: **$<100\text{ms}$ overhead** at 200 concurrent requests.
