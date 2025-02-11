# User Service

## Overview

The **User Service** is a core microservice responsible for user management, authentication, and communication with
other microservices in a distributed system. It handles user registration, authentication, and profile management while
integrating with external services like Kafka for event-driven messaging and JWT for authentication.

## Technologies Used

- **Spring Boot 3.4.2**: Framework for building Java-based microservices.
- **Spring Cloud Config**: Centralized configuration management.
- **Spring Cloud Netflix Eureka Client**: Enables service discovery and registration with Eureka Server.
- **Spring Boot Security**: Provides authentication and authorization features.
- **Spring Boot Validation**: Enables input validation.
- **Spring Boot Web**: Used for building RESTful APIs.
- **Spring Boot JPA**: ORM framework for interacting with the database.
- **PostgreSQL**: Database for storing user information.
- **Kafka**: Event-driven messaging between microservices.
- **Lombok**: Reduces boilerplate code for Java classes.
- **JWT (JSON Web Tokens)**: Secure token-based authentication.
- **Docker**: Containerization for deployment.

## Features

- User registration and authentication.
- JWT-based authentication and authorization.
- User profile management.
- Kafka-based event-driven messaging for user events.
- Database storage using PostgreSQL.
- Integration with Configuration and Discovery services.

## Project Structure

```
user-service/
│── src/main/java/com/fayupable/userservice/
│   ├── config/  # Configuration classes
│   ├── controller/  # REST API controllers
│   ├── dto/  # Data transfer objects
│   ├── entity/  # JPA entities
│   ├── enums/  # Enumerations
│   ├── exception/  # Custom exceptions
│   ├── kafka/  # Kafka event producer
│   ├── mapper/  # Model mapper classes
│   ├── repository/  # Database repository
│   ├── request/  # Request classes
│   ├── response/  # Response classes
│   ├── security/  # Security configuration
│   ├── service/  # Business logic
│── src/main/resources/
│   ├── application.yml  # Configuration file
│── pom.xml  # Dependencies and build configuration
```

## Installation & Setup

### Prerequisites

- Java 21 installed
- Maven installed
- PostgreSQL database set up
- Kafka broker running

### Configuration

Modify `user-service.yml` and `application.yml` to set up the **User Service**:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/user
    username: fayupable
    password: fayupable
    driver-class-name: org.postgresql.Driver

  jpa:
    hibernate:
      ddl-auto: update
    database-platform: org.hibernate.dialect.PostgreSQLDialect
    show-sql: true
  kafka:
    producer:
      bootstrap-servers: localhost:9092
      key-serializer: org.apache.kafka.common.serialization.StringSerializer
      value-serializer: org.springframework.kafka.support.serializer.JsonSerializer
      properties:
        spring.json.add.type.headers: true
        spring.json.type.mapping: userConfirmation:com.fayupable.mailsender.kafka.user.UserConfirmation

server:
  port: 8070

auth:
  token:
    expirationInMils: 3600000
    jwtSecret: 36763979244226452948404D635166546A576D5A7134743777217A25432A4620
```

```yaml
spring:
  config:
    import: optional:configserver:http://localhost:8888
  application:
    name: user-service


```

### Running the Server

1. Clone the repository:
   ```sh
   git clone https://github.com/your-org/user-service.git
   cd user-service
   ```
2. Build the project:
   ```sh
   mvn clean package
   ```
3. Run the application:
   ```sh
   mvn spring-boot:run
   ```

### Testing User Authentication

- **Register a user:**
  ```sh
  curl -X POST http://localhost:8070/api/users/register -H "Content-Type: application/json" -d '{"email":"johndoe@example.com","password":"password"}'
  ```

- **Login:**
  ```sh
  curl -X POST http://localhost:8070/api/users/login -H "Content-Type: application/json" -d '{"email":"johndoe@example.com","password":"password"}'
  ```

- **Get user profile (Authenticated Request):**
  ```sh
  curl -X GET http://localhost:8070/api/users/me -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
  ```

## Kafka Integration

The **User Service** publishes events to Kafka when a user registers or logs in.

### Kafka Producer Log

``` markdown
2025-02-11T15:02:13.083+03:00  INFO 15913 --- [user-service] [nio-8070-exec-4] c.f.mailsender.kafka.UserProducer        : Sending confirmation: UserConfirmation(userId=65045ce9-4b54-4389-b574-d935b9b02fdb, email=johndo3e@ex34ample2.com, verificationCode=cd943fe27c, verificationCodeExpiration=2025-02-11T15:07:13.059396, userLoginTime=null)
```

```json
{
  "userId": "65045ce9-4b54-4389-b574-d935b9b02fdb",
  "email": "johndoe@example.com",
  "verificationCode": "cd943fe27c",
  "verificationCodeExpiration": "2025-02-11T15:07:13.059396",
  "userLoginTime": null
}
```

```markdown
2025-02-11T15:01:40.788+03:00 INFO 15913 --- [user-service] [nio-8070-exec-3] c.f.mailsender.kafka.UserProducer        :
Sending login: UserConfirmation(userId=d1c4d737-3a05-41be-940f-f859de08da73, email=johndoe@ex34ample2.com,
verificationCode=null, verificationCodeExpiration=null, userLoginTime=2025-02-11T15:01:40.788362)
```

```json
{
  "userId": "d1c4d737-3a05-41be-940f-f859de08da73",
  "email": "johndoe@ex34ample2.com",
  "verificationCode": null,
  "verificationCodeExpiration": null,
  "userLoginTime": "2025-02-11T15:01:40.788362"
}
```

## Docker Support

To run the **User Service** inside a Docker container:

1. Create a `Dockerfile`:
   ```Dockerfile
   FROM openjdk:21-jdk
   COPY target/user-service-0.0.1-SNAPSHOT.jar app.jar
   ENTRYPOINT ["java", "-jar", "app.jar"]
   ```
2. Build and run the container:
   ```sh
   docker build -t user-service .
   docker run -p 8070:8070 user-service
   ```

## Common Issues & Troubleshooting

### 1. Database Connection Error

**Error:** `org.postgresql.util.PSQLException: Connection refused`
**Solution:** Ensure PostgreSQL is running and configured correctly.

### 2. Kafka Connection Issue

**Error:** `org.apache.kafka.common.errors.TimeoutException: Timeout expired`
**Solution:** Ensure Kafka broker is running and accessible.

### 3. Authentication Failure

**Error:** `Invalid username or password`
**Solution:** Ensure the user is registered and credentials are correct.

## Conclusion

The **User Service** is a fundamental part of the microservices architecture, handling user authentication and
interaction with external services like Kafka and PostgreSQL. It enables seamless integration with other microservices,
providing a scalable and secure user management solution.

---
