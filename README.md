# E-Commerce REST API

## Overview

This project is a RESTful E-Commerce backend developed using **Spring Boot** following a layered architecture and REST design principles.

The application provides secure APIs for managing users, products, categories, shopping carts, and orders while maintaining a clear separation between the API layer, business logic, and persistence layer. DTOs are used to isolate the API contract from the database entities, and MapStruct is used for object mapping.

Authentication is implemented using **JWT (JSON Web Tokens)** with **Spring Security**, and Role-Based Access Control (RBAC) is used to secure administrative and user-specific operations.

---

# Features

* JWT Authentication
* Spring Security
* Role-Based Access Control (RBAC)
* User Management
* Role Management
* Product Management
* Category Management
* Persisted Shopping Cart
* Order Management
* DTO-based API
* Bean Validation
* Global Exception Handling
* OpenAPI (Swagger) Documentation
* Pagination
* Sorting
* Product Search
* Dynamic Product Filtering
* Layered Architecture
* Repository Pattern
* MapStruct Mapping
* Lombok Integration

---

# Technology Stack

* Java 21
* Spring Boot
* Spring Security
* Spring Web
* Spring Data JPA
* Hibernate
* JWT Authentication
* MapStruct
* Lombok
* Bean Validation
* Maven
* OpenAPI / Swagger
* MySQL

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

DTOs are used to separate the API contract from the persistence model.

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

## Authentication

Handles user authentication using JWT.

Supports:

* User login
* JWT generation
* Stateless authentication
* Protected endpoints

---

## Users

Responsible for managing application users.

Supports:

* Create user
* Retrieve users
* Update user
* Delete user

---

## Roles

Responsible for application authorization.

Supports:

* Assigning user roles
* Role-based access control through Spring Security

---

## Products

Responsible for product catalog management.

Supports:

* Create product
* Retrieve products
* Update product
* Delete product
* Pagination
* Sorting
* Searching by name
* Dynamic filtering by category, price range, and stock availability

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

## Shopping Cart

Each authenticated user owns a persisted shopping cart stored in the database.

Supports:

* View cart
* Add products
* Update quantities
* Remove items
* Clear cart

The cart persists across user sessions and is used during checkout.

---

## Orders

Responsible for customer orders.

Supports:

* Checkout using the authenticated user's shopping cart
* Retrieve orders
* Update order
* Delete order

During checkout the application:

* Validates product availability
* Verifies sufficient stock
* Creates the order
* Creates order items
* Calculates the total automatically
* Deducts inventory
* Clears the shopping cart

All checkout operations execute inside a single transaction.

Orders are initially created with a **PENDING** status.

---

# Security

Authentication and authorization are implemented using Spring Security and JWT.

The application uses Role-Based Access Control (RBAC).

Available roles:

* ADMIN
* USER

Typical authorization rules:

| Endpoint            | Access             |
| ------------------- | ------------------ |
| Authentication      | Public             |
| Product browsing    | Public             |
| Shopping Cart       | Authenticated User |
| Checkout            | Authenticated User |
| User profile        | Owner              |
| Product management  | ADMIN              |
| Category management | ADMIN              |
| View all orders     | ADMIN              |
| View own orders     | USER               |

---

# Design Decisions

* DTOs are used instead of exposing entities directly.
* Business logic is isolated inside the Service layer.
* Persistence is handled through Spring Data JPA repositories.
* Validation is performed using Jakarta Bean Validation.
* Object mapping is performed using MapStruct.
* Controllers coordinate requests and responses only.
* JWT provides stateless authentication.
* RBAC is enforced through Spring Security.
* Shopping carts are persisted in the database.
* Product filtering is implemented using Spring Data JPA Specifications.
* Pagination and sorting use Spring Data's Pageable abstraction.
* The project follows RESTful API conventions.

---

# API Documentation

The complete API specification is available through the generated OpenAPI 3 documentation.

Swagger UI provides:

* Available endpoints
* Request DTOs
* Response DTOs
* Validation constraints
* Authentication support
* HTTP methods
* Status codes

---

# Current Resources

The API currently exposes endpoints for:

* Authentication
* Users
* Roles
* Products
* Categories
* Shopping Cart
* Orders

---

# Future Improvements

- Refresh Token support
- Product image upload
- Order history enhancements
- Unit Testing
- Integration Testing
- Docker deployment
- CI/CD pipeline

# Running the Project

1. Clone the repository.
2. Configure the MySQL database.
3. Configure application properties.
4. Run the application.

```
mvn spring-boot:run
```

or run the main application class from your IDE.

---

# API Testing

The API can be tested using:

* Postman Collection
* Swagger UI
* OpenAPI 3 Specification
