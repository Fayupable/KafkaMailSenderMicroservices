# API Gateway Service

## Overview

The **API Gateway Service** acts as a single entry point for all requests in a microservices architecture. It routes,
filters, and secures incoming requests before forwarding them to appropriate services. API Gateway simplifies client
interactions by providing a unified interface for multiple backend services.

## Technologies Used

- **Spring Boot 3.4.2**: Java-based framework for building microservices.
- **Spring Cloud Gateway 2024.0.0**: A gateway for microservices that provides routing, filtering, and security.
- **Spring Cloud Netflix Eureka 2024.0.0**: Enables service discovery and client-side load balancing.
- **Java 21**: Programming language used for development.
- **Maven**: Dependency and build management tool.

## Features

- Centralized routing for microservices.
- Load balancing via Eureka Discovery.
- Path-based routing.
- Integration with Spring Cloud Config Server for dynamic configuration.
- Scalability and security enhancements.

## How It Works

1. **Client Request Handling**: Clients send requests to API Gateway instead of directly interacting with microservices.
2. **Service Discovery**: Gateway dynamically resolves service instances using Eureka.
3. **Routing**: Requests are forwarded to the appropriate microservice based on pre-defined rules.
4. **Load Balancing**: API Gateway distributes incoming traffic to available instances of a service.
5. **Filtering**: Incoming requests can be modified, authenticated, or transformed before reaching backend services.

## Project Structure

```
gateway-service/
│── src/main/java/com/fayupable/gateway/
│   ├── GatewayApplication.java  # Main class
│── src/main/resources/
│   ├── application.yml  # Configuration file
│   ├── gateway-service.yml  # Routing rules
│── pom.xml  # Dependencies and build configuration
```

## Installation & Setup

### Prerequisites

- Java 21 installed.
- Maven installed.
- Spring Cloud Config Server is running at `http://localhost:8888`.
- Eureka Discovery Service is running at `http://localhost:8761`.

### Configuration

Modify `application.yml` and `gateway-service.yml` to set up the **Gateway Service**.

```yaml
spring:
  config:
    import: optional:configserver:http://localhost:8888
  application:
    name: gateway-service
```

```yaml
spring:
  cloud:
    gateway:
      discovery:
        locator:
          enabled: true
      routes:
        - id: user-service
          uri: lb://user-service
          predicates:
            - Path=/user/**

server:
  port: 8222

```

### Running the Server

1. Clone the repository:
   ```sh
   git clone https://github.com/your-org/gateway-service.git
   cd gateway-service
   ```
2. Build the project:
   ```sh
   mvn clean package
   ```
3. Run the application:
   ```sh
   mvn spring-boot:run
   ```

### Testing the API Gateway

To test if the **API Gateway** is running, open a web browser or use `curl`:

```
http://localhost:8222
```

Assuming the User Service is registered with Eureka, you can send a request:

```sh
curl http://localhost:8222/user/profile
```

API Gateway will automatically route this request to the appropriate User Service instance.

## Integrating with Microservices

To register microservices with the **Gateway Service**, ensure that each microservice:

- Has Eureka Client enabled.
- Has a valid `application.yml` configuration.
- Uses Spring Cloud Config Server (if applicable).

Example for User Service:

```yaml
spring:
  application:
    name: user-service
  cloud:
    config:
      import: optional:configserver:http://localhost:8888

eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/
    instance:
      hostname: localhost
```

## Docker Support

To run the **API Gateway Service** inside a Docker container:

1. Create a `Dockerfile`:
   ```Dockerfile
   FROM openjdk:21-jdk
   COPY target/gateway-service-0.0.1-SNAPSHOT.jar app.jar
   ENTRYPOINT ["java", "-jar", "app.jar"]
   ```
2. Build and run the container:
   ```sh
   docker build -t gateway-service .
   docker run -p 8222:8222 gateway-service
   ```

## Common Issues & Troubleshooting

### 1. Gateway Not Starting

**Error:** Port 8222 is already in use  
**Solution:** Ensure the port is not occupied by another process or modify `application.yml` to use a different port.

### 2. Microservice Not Found

**Error:** Service Not Found in Eureka  
**Solution:** Ensure that:

- The microservice is correctly registered with Eureka.
- Eureka is running at `http://localhost:8761`.

### 3. Requests Timing Out

**Error:** Gateway Timeout (504)  
**Solution:**

- Verify that the target service is running and accessible.
- Check Eureka Dashboard to confirm service registration.

## Conclusion

The **API Gateway Service** is a vital component of a microservices architecture. It provides a unified entry point,
efficient request routing, load balancing, and enhanced security. With Spring Cloud Gateway, it ensures a scalable,
resilient, and flexible communication layer for backend services.