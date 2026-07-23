# E-Commerce REST API

## Overview

This project is a RESTful E-Commerce backend developed using **Spring Boot** following a layered architecture and REST design principles.

The application provides APIs for managing users, products, categories, and orders while maintaining a clear separation between the API layer, business logic, and persistence layer. DTOs are used to isolate the API contract from the database entities, and MapStruct is used for object mapping.

The project is designed to be easily extended with **Spring Security** and **JWT Authentication**, allowing role-based access to protected resources.

---

# Features

* User Management
* Product Management
* Category Management
* Order Management
* Order Item Management
* DTO-based API
* Bean Validation
* Global Exception Handling
* OpenAPI (Swagger) Documentation
* Layered Architecture
* Repository Pattern
* MapStruct Mapping
* Lombok Integration

---

# Technology Stack

* Java 21
* Spring Boot
* Spring Web
* Spring Data JPA
* Hibernate
* MapStruct
* Lombok
* Bean Validation
* Maven
* OpenAPI / Swagger
* H2 / MySQL (depending on configuration)

---

# Architecture

The application follows a layered architecture:

```
Controller
      │
      ▼
Service
      │
      ▼
Repository
      │
      ▼
Database
```

Additionally, DTOs are used to transfer data between the API and the service layer.

```
Client
   │
   ▼
Controller
   │
Request DTO
   │
Service
   │
Entity
   │
Repository
   │
Database

Database
   │
Entity
   │
Service
   │
Response DTO
   │
Controller
   │
Client
```

---

# Main Modules

## Users

Responsible for managing application users.

Supports:

* Create user
* Retrieve users
* Update user
* Delete user

---

## Products

Responsible for product catalog management.

Supports:

* Create product
* Retrieve products
* Update product
* Delete product

Products may belong to one or more categories.

---

## Categories

Responsible for organizing products.

Supports:

* Create category
* Retrieve categories
* Update category
* Delete category

---

## Orders

Responsible for customer orders.

Supports:

* Create order
* Retrieve orders
* Update order
* Delete order

Each order contains one or more order items.

The total order amount is calculated automatically from the products and quantities supplied.

Orders are initially created with a **PENDING** status.

A future enhancement will allow privileged users to update an order's status (e.g., SHIPPED) through secured endpoints.

---

# Design Decisions

* DTOs are used instead of exposing entities directly.
* Business logic is isolated inside the Service layer.
* Persistence is handled through Spring Data JPA repositories.
* Validation is performed using Jakarta Bean Validation.
* Object mapping is performed using MapStruct.
* Controllers only coordinate requests and responses.
* The project follows RESTful API conventions.

---

# Security (Planned)

The application is designed to support:

* Spring Security
* JWT Authentication
* Role-Based Authorization

Planned roles include:

* ADMIN
* USER

Typical authorization rules:

| Endpoint             | Access             |
| -------------------- | ------------------ |
| Authentication       | Public             |
| Product browsing     | Public             |
| Order creation       | Authenticated User |
| User profile         | Owner              |
| Product management   | Admin              |
| Category management  | Admin              |
| Order status updates | Admin              |

---

# API Documentation

The complete API specification is available through the generated OpenAPI 3 documentation.

```
OpenAPI3.json
```

or through Swagger UI after running the application.

The documentation contains:

* Available endpoints
* Request DTOs
* Response DTOs
* Validation constraints
* Response schemas
* HTTP methods
* Status codes

---

# Current Resources

The API currently exposes endpoints for:

* Users
* Products
* Categories
* Orders
* Roles

Each resource supports the appropriate REST operations (GET, POST, PUT, DELETE) where applicable.

Future versions will include authentication endpoints and secured administrative operations.

---

# Future Improvements

* JWT Authentication
* Spring Security
* Role-based authorization
* Order status update endpoint
* Pagination
* Filtering and searching
<!-- * Product image support
* Order history
* Refresh Tokens -->
* Unit and Integration Testing

---

# Running the Project

1. Clone the repository.
2. Configure the database.
3. Run the Spring Boot application.

```
mvn spring-boot:run
```

or run the main application class from your IDE.

---

# API Testing

The project can be tested using:

* Postman Collection
* Swagger UI
* OpenAPI 3 Specification

---

# Authors

Developed as a project using Spring Boot and modern REST API design principles.
