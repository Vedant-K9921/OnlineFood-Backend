# LocalBites — Backend 🍔

The backend service powering **LocalBites**, a full-stack online food ordering platform. Built with **Spring Boot** and **Java**, it handles authentication, order management, real-time order tracking, payments, and more — designed to serve a React-based frontend for Customer, Owner (restaurant), and Admin roles.

> ⚠️ This project is under active development.

---

## 🚀 Tech Stack

- **Java 17**
- **Spring Boot 3.5.0**
  - Spring Web (REST APIs)
  - Spring Data JPA
  - Spring Security
  - Spring Validation
  - Spring WebSocket (real-time order tracking)
  - Spring Mail
- **PostgreSQL** — Primary database
- **JWT (JJWT 0.12.6)** — Stateless authentication & authorization
- **Razorpay Java SDK** — Payment gateway integration
- **Lombok** — Boilerplate reduction
- **springdoc-openapi (Swagger UI)** — API documentation
- **Maven** — Build & dependency management

---

## ✨ Features

- 🔐 **JWT-based Authentication & Authorization** — secure login/signup with role-based access control
- 🛒 **Order Management** — create, track, and update food orders
- 📡 **Real-Time Order Tracking** — WebSocket-powered live status updates
- 💳 **Payments** — Razorpay integration for online transactions
- 📧 **Email Notifications** — via Spring Mail
- 📑 **API Documentation** — interactive Swagger UI via springdoc-openapi
- 👥 **Multi-Role Support** — separate flows for Customers, Restaurant Owners, and Admins

---

## 📁 Project Structure

```
OnlineFood-Backend/
├── src/
│   ├── main/
│   │   ├── java/com/localbites/backend/   # Application source code
│   │   │   ├── controller/                # REST controllers
│   │   │   ├── service/                   # Business logic
│   │   │   ├── repository/                # JPA repositories
│   │   │   ├── model/ (entity)            # Database entities
│   │   │   ├── dto/                       # Data transfer objects
│   │   │   ├── security/                  # JWT & Spring Security config
│   │   │   ├── websocket/                 # WebSocket configuration
│   │   │   └── config/                    # App-level configuration
│   │   └── resources/
│   │       └── application.properties     # App configuration
│   └── test/                              # Unit & integration tests
├── .mvn/wrapper/                          # Maven wrapper
├── mvnw / mvnw.cmd                        # Maven wrapper scripts
├── pom.xml                                # Maven project configuration
└── .gitignore
```

*(Package layout above reflects a typical Spring Boot structure for this project — adjust to match your actual source tree if it differs.)*

---

## ⚙️ Getting Started

### Prerequisites

- **Java 17** or later
- **Maven** (or use the included Maven Wrapper — no local install needed)
- **PostgreSQL** database instance
- A **Razorpay** account (test/live API keys) for payment integration

### 1. Clone the Repository

```bash
git clone https://github.com/Vedant-K9921/OnlineFood-Backend.git
cd OnlineFood-Backend
```

### 2. Configure the Database & Secrets

Create/update `src/main/resources/application.properties` (or use environment variables) with your own values:

```properties
# Database
spring.datasource.url=jdbc:postgresql://localhost:5432/localbites
spring.datasource.username=your_db_username
spring.datasource.password=your_db_password
spring.jpa.hibernate.ddl-auto=update

# JWT
jwt.secret=your_jwt_secret_key
jwt.expiration=86400000

# Razorpay
razorpay.key.id=your_razorpay_key_id
razorpay.key.secret=your_razorpay_key_secret

# Mail
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your_email@gmail.com
spring.mail.password=your_email_app_password
```

> 🔒 **Never commit real secrets.** Use environment variables or a `.env`/secrets manager in production, and keep `application.properties` (with real credentials) out of version control.

### 3. Build & Run

Using the Maven Wrapper:

```bash
./mvnw clean install
./mvnw spring-boot:run
```

Or with a local Maven install:

```bash
mvn clean install
mvn spring-boot:run
```

The server will start on `http://localhost:8080` by default.

### 4. API Documentation

Once running, view the interactive Swagger UI at:

```
http://localhost:8080/swagger-ui.html
```

---

## 🔌 API Overview

| Module         | Description                                      |
|----------------|---------------------------------------------------|
| `/auth`        | Signup, login, JWT token issuance                 |
| `/orders`      | Create, view, and update food orders               |
| `/payments`    | Razorpay checkout & payment verification           |
| `/ws`          | WebSocket endpoint for real-time order tracking    |
| `/admin`       | Admin-only management endpoints                    |

*(Exact endpoints depend on the current controllers in `src/main/java` — see Swagger UI for the live, authoritative list.)*

---

## 🗺️ Roadmap

- [ ] Complete Swagger/OpenAPI annotations for all endpoints
- [ ] Add automated tests (unit + integration)
- [ ] Dockerize the application
- [ ] CI/CD pipeline (GitHub Actions)
- [ ] Deploy to a cloud provider (Render/Railway/AWS)
- [ ] Rate limiting & request logging

---

## 🤝 Contributing

This is currently a solo/portfolio project. Suggestions, issues, and forks are welcome.

---

## 📄 License

No license has been specified yet. All rights reserved by the author unless a license is added.

---

## 👤 Author

**Vedant Kamble**
[GitHub: @Vedant-K9921](https://github.com/Vedant-K9921)
