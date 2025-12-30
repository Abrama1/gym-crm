# 🏋️‍♂️ Gym CRM System

[![Java](https://img.shields.io/badge/Java-21-orange)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen)](https://spring.io/projects/spring-boot)
[![JUnit 5](https://img.shields.io/badge/Testing-JUnit%205-blue)](https://junit.org/junit5/)

> A modular backend system for managing **trainers, trainees, and trainings**, built during the **EPAM Java Internship**.  
> Features JWT-based authentication, Spring Security, Micrometer metrics, and role-based authorization.

---
- **gym-crm (main service)**

- **discovery-server (Eureka)**

- **workload-service (trainer workload microservice)**
---

## 🚀 Features

- ✅ **Spring Boot migration** for modular, profile-based configuration  
- 🔐 **Authentication & Authorization**
  - BCrypt password hashing (salted + adaptive)
  - JWT bearer tokens with logout + token blacklist
  - Brute-force protection (3 failed attempts → 5 min lockout)
- 👥 **Role-based user management**
  - Create/update/delete trainee & trainer profiles
  - Automatic username & random password generation
- 🧠 **Training management**
  - Create and list trainings by date/type
  - Trainee-trainer assignment & filtering
- 📈 **Metrics and Observability**
  - Micrometer counters and timers exposed via Prometheus endpoint
- 🧪 **Comprehensive testing**
  - JUnit 5 & Mockito for service and controller layers

---

## 🧩 Tech Stack

| Layer | Technology |
|-------|-------------|
| **Language** | Java 21 |
| **Framework** | Spring Boot 3.x |
| **Security** | Spring Security, JWT, BCrypt |
| **Database** | PostgreSQL |
| **Persistence** | JPA / Hibernate |
| **Monitoring** | Micrometer + Prometheus |
| **Testing** | JUnit 5, Mockito |
| **Build Tool** | Maven |

---

<details>
<summary>📁 <b>Project Structure</b></summary>

```
gym-crm/
 ├── src/main/java/com/example/gymcrm/
 │   ├── api/                 # REST Controllers
 │   ├── config/              # App + Filter Configs
 │   ├── dao/                 # DAO Interfaces
 │   ├── dto/                 # Request/Response DTOs
 │   ├── entity/              # JPA Entities
 │   ├── exceptions/          # Custom Exceptions
 │   ├── security/            # JWT, Filters, BruteForce
 │   ├── service/             # Interfaces & Implementations
 │   └── util/                # Helper Utilities
 ├── src/test/java/...        # Unit + Integration Tests
 └── resources/
     ├── application.yml
     ├── application-dev.yml
     ├── application-prod.yml
     └── application-local.yml
```
</details>

---

## ⚙️ Getting Started

### 🧭 Prerequisites
- Java 21+
- Maven 3.9+
- PostgreSQL

### 🔧 Setup & Run

```bash
# 1. Clone repository
git clone https://github.com/yourusername/gym-crm.git
cd gym-crm

# 2. Configure PostgreSQL credentials
# edit src/main/resources/application-local.yml

# 3. Build and run
mvn clean spring-boot:run
```

App runs at: [http://localhost:8080](http://localhost:8080)

---

## 🔐 Example API Endpoints

| Method | Endpoint | Description | Auth |
|--------|-----------|-------------|------|
| `POST` | `/api/trainees/register` | Register new trainee | Public |
| `POST` | `/api/trainers/register` | Register new trainer | Public |
| `POST` | `/api/auth/login` | Login and obtain JWT token | Public |
| `GET` | `/api/trainees/{username}` | Get trainee profile | JWT |
| `PUT` | `/api/trainees/{username}` | Update trainee profile | JWT |
| `GET` | `/api/trainers/{username}` | Get trainer profile | JWT |
| `POST` | `/api/trainings` | Create training | JWT |
| `GET` | `/api/trainers/{username}/trainings` | List trainer’s trainings | JWT |
| `GET` | `/actuator/prometheus` | Prometheus metrics | Internal |

---

<details>
<summary>📊 <b>Available Metrics</b></summary>

| Metric | Description |
|--------|-------------|
| `gym_registrations_total` | Number of user registrations (by role) |
| `gym_profile_activations_total` | Profile activations/deactivations |
| `gymcrm.trainings.created` | Trainings created |
| `gymcrm.trainings.list` | List operation timings |
| `gym_auth_attempts_total` | Auth success/failure counts |
</details>

---

## 🧪 Running Tests

```bash
mvn test
```

Tests include:
- Service layer (Trainer, Trainee, Training, Auth)
- Controller layer with MockMvc
- Security (JWT + Brute-force scenarios)

---

## 👨‍💻 Contributors

| Name | Role |
|------|------|
| **Davit Abramishvili** | Backend Developer (EPAM Java Intern) |
| _Pavel Dziuubanau_ | Mentor/Senior Software Engineer |

---
