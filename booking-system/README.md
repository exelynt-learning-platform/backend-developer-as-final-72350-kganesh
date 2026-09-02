# Resource Booking System

A RESTful **Resource Booking System** built using **Spring Boot, Java, Spring Security, JWT, JPA, and MySQL/PostgreSQL**.

The system allows users to view available resources and create reservations, while administrators can manage resources and reservations through role-based access control.

---

## 🚀 Features

### Authentication & Authorization

* User registration
* User login
* JWT-based authentication
* Role-based access control
* Supports `USER` and `ADMIN` roles
* Passwords are securely stored using BCrypt
* Stateless authentication using JWT

### Resource Management

**ADMIN**

* Create resources
* View all resources
* View resource by ID
* Update resources
* Delete resources

**USER**

* View all resources
* View resource by ID

### Reservation Management

**USER**

* Create reservations
* View own reservations
* View own reservation by ID
* Cancel own reservation

**ADMIN**

* View all reservations
* View any reservation
* Update reservation status
* Filter reservations
* Paginate reservations
* Sort reservations

### Reservation Validation

* Start time must be in the future
* End time must be in the future
* End time must be after start time
* Resource must be available
* Prevents overlapping reservations
* Prevents invalid price filters
* Reservation price is captured from the resource price
* Reservation status supports:

  * `PENDING`
  * `CONFIRMED`
  * `CANCELLED`

### Exception Handling

Centralized exception handling using `@RestControllerAdvice`.

The application provides meaningful HTTP responses for:

* Resource not found
* Reservation not found
* Unauthorized access
* Reservation conflicts
* Invalid request data
* Invalid reservation status
* Invalid role
* Duplicate username

---

## 🛠️ Technologies Used

| Technology         | Purpose                        |
| ------------------ | ------------------------------ |
| Java 17+           | Programming language           |
| Spring Boot        | Backend framework              |
| Spring Web         | REST APIs                      |
| Spring Security    | Authentication & authorization |
| JWT                | Token-based authentication     |
| Spring Data JPA    | Database access                |
| Hibernate          | ORM                            |
| MySQL / PostgreSQL | Database                       |
| Maven              | Dependency management          |
| Jakarta Validation | Request validation             |
| Postman            | API testing                    |

---

## 📁 Project Structure

```text
src
└── main
    └── java
        └── com.ganesh.booking_system
            │
            ├── controller
            │   ├── AuthController.java
            │   ├── ResourceController.java
            │   └── ReservationController.java
            │
            ├── dto
            │   ├── LoginRequest.java
            │   ├── LoginResponse.java
            │   ├── RegisterRequest.java
            │   ├── RegisterResponse.java
            │   ├── ResourceRequest.java
            │   ├── ResourceResponse.java
            │   ├── ReservationRequest.java
            │   └── ReservationResponse.java
            │
            ├── entity
            │   ├── User.java
            │   ├── Resource.java
            │   └── Reservation.java
            │
            ├── enums
            │   ├── Role.java
            │   └── ReservationStatus.java
            │
            ├── exception
            │   ├── BadRequestException.java
            │   ├── ErrorResponse.java
            │   ├── GlobalExceptionHandler.java
            │   ├── ReservationConflictException.java
            │   ├── ReservationNotFoundException.java
            │   ├── ResourceNotFoundException.java
            │   └── UnauthorizedException.java
            │
            ├── repository
            │   ├── UserRepository.java
            │   ├── ResourceRepository.java
            │   └── ReservationRepository.java
            │
            ├── security
            │   ├── JwtAuthenticationFilter.java
            │   ├── JwtService.java
            │   └── SecurityConfig.java
            │
            ├── service
            │   ├── UserService.java
            │   ├── ResourceService.java
            │   ├── ReservationService.java
            │   └── CustomUserDetailsService.java
            │
            ├── service.impl
            │   ├── UserServiceImpl.java
            │   ├── ResourceServiceImpl.java
            │   └── ReservationServiceImpl.java
            │
            ├── specification
            │   └── ReservationSpecification.java
            │
            └── validation
                ├── ValidReservationTime.java
                └── ReservationTimeValidator.java
```

---

# 🔐 Authentication API

## Register User

```http
POST /auth/register
```

### Request

```json
{
    "username": "john",
    "password": "password123",
    "role": "USER"
}
```

### Response

**201 Created**

```json
{
    "id": 1,
    "username": "john",
    "role": "USER"
}
```

---

## Login

```http
POST /auth/login
```

### Request

```json
{
    "username": "john",
    "password": "password123"
}
```

### Response

**200 OK**

```json
{
    "token": "YOUR_JWT_TOKEN"
}
```

Use the returned token for protected endpoints:

```http
Authorization: Bearer YOUR_JWT_TOKEN
```

---

# 📦 Resource APIs

## Create Resource

**ADMIN only**

```http
POST /resources
```

### Request

```json
{
    "name": "Conference Room",
    "description": "Large conference room",
    "price": 1500.00,
    "available": true
}
```

---

## Get All Resources

**USER / ADMIN**

```http
GET /resources
```

---

## Get Resource By ID

**USER / ADMIN**

```http
GET /resources/{id}
```

Example:

```http
GET /resources/1
```

---

## Update Resource

**ADMIN only**

```http
PUT /resources/{id}
```

### Request

```json
{
    "name": "Updated Conference Room",
    "description": "Updated description",
    "price": 1800.00,
    "available": true
}
```

---

## Delete Resource

**ADMIN only**

```http
DELETE /resources/{id}
```

---

# 📅 Reservation APIs

## Create Reservation

**USER only**

```http
POST /reservations
```

### Request

```json
{
    "resourceId": 1,
    "startTime": "2026-09-10T10:00:00",
    "endTime": "2026-09-10T12:00:00"
}
```

The reservation price is automatically taken from the resource's current price.

### Response

**201 Created**

```json
{
    "id": 1,
    "resourceId": 1,
    "resourceName": "Conference Room",
    "userId": 2,
    "username": "john",
    "startTime": "2026-09-10T10:00:00",
    "endTime": "2026-09-10T12:00:00",
    "price": 1500.00,
    "status": "PENDING"
}
```

---

## Get My Reservations

**USER only**

```http
GET /reservations/my
```

Returns only reservations belonging to the authenticated user.

---

## Get Reservation By ID

**USER / ADMIN**

```http
GET /reservations/{id}
```

A USER can access only their own reservation.

An ADMIN can access any reservation.

---

## Get All Reservations

**ADMIN only**

```http
GET /reservations
```

### Filter by Status

```http
GET /reservations?status=PENDING
```

Supported statuses:

```text
PENDING
CONFIRMED
CANCELLED
```

### Filter by Minimum Price

```http
GET /reservations?minPrice=1000
```

### Filter by Maximum Price

```http
GET /reservations?maxPrice=3000
```

### Combine Filters

```http
GET /reservations?status=CONFIRMED&minPrice=1000&maxPrice=3000
```

---

## Pagination

Default:

```http
GET /reservations?page=0&size=10
```

Example:

```http
GET /reservations?page=1&size=5
```

---

## Sorting

Example:

```http
GET /reservations?sort=price,asc
```

Another example:

```http
GET /reservations?sort=createdAt,desc
```

Default sorting:

```text
createdAt DESC
```

---

## Update Reservation Status

**ADMIN only**

```http
PUT /reservations/{id}/status?status=CONFIRMED
```

Supported statuses:

```text
PENDING
CONFIRMED
CANCELLED
```

---

## Cancel Reservation

**USER only**

```http
PUT /reservations/{id}/cancel
```

A user can cancel only their own reservation.

The reservation status is changed to:

```text
CANCELLED
```

---

# 👥 Role-Based Access Control

| Endpoint                        |   USER   | ADMIN |
| ------------------------------- | :------: | :---: |
| `POST /auth/register`           |     ✅    |   ✅   |
| `POST /auth/login`              |     ✅    |   ✅   |
| `POST /resources`               |     ❌    |   ✅   |
| `GET /resources`                |     ✅    |   ✅   |
| `GET /resources/{id}`           |     ✅    |   ✅   |
| `PUT /resources/{id}`           |     ❌    |   ✅   |
| `DELETE /resources/{id}`        |     ❌    |   ✅   |
| `POST /reservations`            |     ✅    |   ❌   |
| `GET /reservations/my`          |     ✅    |   ❌   |
| `GET /reservations`             |     ❌    |   ✅   |
| `GET /reservations/{id}`        | Own only |  Any  |
| `PUT /reservations/{id}/status` |     ❌    |   ✅   |
| `PUT /reservations/{id}/cancel` | Own only |   ❌   |

---

# 🗄️ Database Configuration

Create a database before running the application.

Example for MySQL:

```sql
CREATE DATABASE booking_system;
```

Configure the database in:

```text
src/main/resources/application.properties
```

Example:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/booking_system
spring.datasource.username=root
spring.datasource.password=YOUR_PASSWORD

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

jwt.secret=YOUR_SECRET_KEY
jwt.expiration=3600000
```

> Replace the database credentials and JWT secret with your local configuration.

---

# ▶️ How to Run

## 1. Clone the repository

```bash
git clone <YOUR_GITHUB_REPOSITORY_URL>
```

## 2. Navigate to the project

```bash
cd booking_system
```

## 3. Configure the database

Create the `booking_system` database and update `application.properties`.

## 4. Build the project

```bash
mvn clean install
```

## 5. Run the application

```bash
mvn spring-boot:run
```

The application will start on:

```text
http://localhost:8080
```

---

# 🧪 API Testing

The APIs can be tested using **Postman**.

Recommended testing flow:

```text
1. Register ADMIN
       ↓
2. Register USER
       ↓
3. Login ADMIN
       ↓
4. Create Resource
       ↓
5. Login USER
       ↓
6. View Resources
       ↓
7. Create Reservation
       ↓
8. View My Reservations
       ↓
9. ADMIN confirms reservation
       ↓
10. USER cancels reservation
```

The application also validates unauthorized access, invalid input, overlapping reservations, unavailable resources, invalid statuses, filtering, pagination, and sorting.

---

# 🔒 Security

The application uses:

* Spring Security
* JWT authentication
* BCrypt password hashing
* Stateless sessions
* Role-based authorization
* Method-level security using `@PreAuthorize`

Passwords are never returned through API responses.

JWT tokens are required for protected endpoints.

---

# 📌 HTTP Status Codes

| Status                      | Meaning                                     |
| --------------------------- | ------------------------------------------- |
| `200 OK`                    | Successful request                          |
| `201 CREATED`               | Resource/reservation successfully created   |
| `204 NO CONTENT`            | Successful deletion/cancellation            |
| `400 BAD REQUEST`           | Invalid input or request                    |
| `401 UNAUTHORIZED`          | Authentication required/invalid credentials |
| `403 FORBIDDEN`             | Insufficient permissions                    |
| `404 NOT FOUND`             | Resource/reservation not found              |
| `409 CONFLICT`              | Reservation time conflict                   |
| `500 INTERNAL SERVER ERROR` | Unexpected server error                     |

---

# 📋 Assignment Requirements

The project implements the following requirements:

* [x] RESTful Spring Boot application
* [x] JWT authentication
* [x] USER and ADMIN roles
* [x] Role-based access control
* [x] Resource CRUD
* [x] User resource read access
* [x] Reservation creation
* [x] User's own reservation access
* [x] Admin reservation management
* [x] Reservation cancellation
* [x] Reservation status management
* [x] Reservation conflict detection
* [x] Resource availability validation
* [x] Price filtering
* [x] Status filtering
* [x] Pagination
* [x] Sorting
* [x] Request validation
* [x] Global exception handling
* [x] Secure password storage
* [x] DTO-based API responses

---

# 👨‍💻 Author

**K. Ganesh Krishna**

Backend Developer | Java | Spring Boot

---

## 📄 License

This project was developed as a backend development assignment and is intended for educational and evaluation purposes.
