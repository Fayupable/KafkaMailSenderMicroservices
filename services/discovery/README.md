# Discovery Service

## Overview

The **Discovery Service** is a critical component in a microservices architecture, providing service registration and
discovery capabilities. It allows microservices to find and communicate with each other without hardcoding their network
locations. This setup enhances scalability, fault tolerance, and dynamic configuration of services.

## Technologies Used

- **Spring Boot 3.4.2**: Framework for building Java applications.
- **Spring Cloud Netflix Eureka 2024.0.0**: Provides service registration and discovery.
- **Java 21**: The programming language used.
- **Maven**: Build automation and dependency management.

## Features

- Service registration and discovery.
- Health checks for registered services.
- Load balancing and failover support.
- Dynamic scaling of services.
- Secure access to the discovery server.

## How It Works

1. **Service Registration**: Microservices register themselves with the **Discovery Service** upon startup.
2. **Service Discovery**: Microservices query the **Discovery Service** to find the network locations of other services.
3. **Health Checks**: The **Discovery Service** periodically checks the health of registered services.
4. **Load Balancing**: Requests are distributed among available instances of a service.
5. **Failover**: If a service instance fails, requests are redirected to healthy instances.

## Project Structure

```
discovery-service/
│── src/main/java/com/fayupable/discoveryservice/
│   ├── DiscoveryServiceApplication.java  # Main class
│── src/main/resources/
│   ├── application.yml  # Server configuration file
│── pom.xml  # Dependencies and build configuration
```

## Installation & Setup

### Prerequisites

- Java 21 installed
- Maven installed

### Configuration

Modify `application.yml` to set up the **Discovery Service**.

```yaml
server:
  port: 8761

eureka:
  client:
    register-with-eureka: false
    fetch-registry: false
  instance:
    hostname: localhost
```

### Running the Server

1. Clone the repository:
   ```sh
   git clone https://github.com/your-org/discovery-service.git
   cd discovery-service
   ```
2. Build the project:
   ```sh
   mvn clean package
   ```
3. Run the application:
   ```sh
   mvn spring-boot:run
   ```

### Testing the Discovery Service

To test if the **Discovery Service** is running, open a web browser and navigate to:

```
http://localhost:8761
```

You should see the Eureka dashboard displaying registered services.

## Integrating with Microservices

To integrate a microservice with the **Discovery Service**, add the following configuration to the `application.yml` and `discoveryservice.yml`
file:


```yaml
spring:
  config:
    import: optional:configserver:http://localhost:8888
  application:
    name: discovery-service
```

```yaml
eureka:
  instance:
    hostname: localhost
  client:
    registerWithEureka: false
    fetchRegistry: false
    serviceUrl:
      defaultZone: http://${eureka.instance.hostname}:${server.port}/eureka/
server:
  port: 8761
```

This will allow the microservice to register with the **Discovery Service** at `http://localhost:8761/eureka/`.

## Docker Support

To run the **Discovery Service** inside a Docker container:

1. Create a `Dockerfile`:
   ```Dockerfile
   FROM openjdk:21-jdk
   COPY target/discovery-service-0.0.1-SNAPSHOT.jar app.jar
   ENTRYPOINT ["java", "-jar", "app.jar"]
   ```
2. Build and run the container:
   ```sh
   docker build -t discovery-service .
   docker run -p 8761:8761 discovery-service
   ```

## Common Issues & Troubleshooting

### 1. Discovery Service Not Starting

**Error:** `Failed to bind to port`
**Solution:** Ensure the port `8761` is not already in use or change the port in `application.yml`.

### 2. Service Not Registering

**Error:** `Service not found in Eureka`
**Solution:** Ensure the microservice is configured correctly to register with the **Discovery Service**.

### 3. Connection Refused Error

**Error:** `I/O error on GET request for "http://localhost:8761/eureka": Connection refused`
**Solution:** Ensure the **Discovery Service** is running and accessible at `localhost:8761`.

## Conclusion

The **Discovery Service** is a vital part of a microservices architecture, providing dynamic service registration and
discovery. By centralizing service information, it enhances scalability, fault tolerance, and simplifies service
communication.