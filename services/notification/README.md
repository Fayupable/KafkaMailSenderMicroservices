# Notification Service

## Overview

The **Notification Service** is responsible for handling notifications within the system. It listens for events and sends notifications via email or other communication channels. This microservice is integrated with **Kafka** for asynchronous message handling and uses **PostgreSQL** as a database for storing notification logs.

## Technologies Used

- **Spring Boot 3.4.2**: A framework for building Java-based applications.
- **Spring Cloud Config**: Centralized configuration management.
- **Spring Cloud Netflix Eureka Client**: Enables service discovery and registration.
- **Spring Kafka**: Facilitates event-driven communication between microservices.
- **Spring Boot Starter Mail**: Used for sending email notifications.
- **Spring Boot Starter Thymeleaf**: Supports email templating with Thymeleaf.
- **Spring Boot Starter Data JPA**: Provides ORM and database interaction.
- **PostgreSQL**: Relational database for persisting notification data.
- **Lombok**: Reduces boilerplate code in Java.
- **Jackson Datatype JSR310**: Supports Java 8 date/time serialization.
- **Maven**: Build automation and dependency management.

## Features

- Listens for notification events via Kafka.
- Sends email notifications using SMTP.
- Stores notification logs in PostgreSQL.
- Fetches configurations from the **Config Server**.
- Registers itself with **Eureka Discovery Service**.

## How It Works

1. **Event Listening**: Listens to Kafka topics for incoming events that trigger notifications.
2. **Email Notification**: Sends emails based on predefined templates using Spring Boot Mail.
3. **Database Logging**: Stores sent notifications in the PostgreSQL database.
4. **Configuration Management**: Retrieves settings from the Config Server.
5. **Service Registration**: Registers itself with Eureka for service discovery.

## Project Structure

```
notification-service/
│── src/main/java/com/fayupable/notification/
│   ├── NotificationServiceApplication.java  # Main class
│   ├── config/  # Configuration classes
│   ├── entity/  # JPA entities
│   ├── enums/  # Enumerations
│   ├── service/  # Business logic
│   ├── repository/  # Database repository
|   ├── kafka/  # Kafka event listener
│── src/main/resources/
│   ├── application.yml  # Service configuration file
│── pom.xml  # Dependencies and build configuration
```

## Installation & Setup

### Prerequisites

- Java 21 installed
- Maven installed
- PostgreSQL database running
- Kafka broker running

### Configuration

Modify `notification-service.yml` and `application.yml` to configure the **Notification Service**.

```yaml
server:
  port: 8040
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/notification
    username: fayupable
    password: fayupable
    driver-class-name: org.postgresql.Driver

  jpa:
    hibernate:
      ddl-auto: update
    database-platform: org.hibernate.dialect.PostgreSQLDialect
    show-sql: true

  kafka:
    consumer:
      bootstrap-servers: localhost:9092
      group-id: user-group
      auto-offset-reset: earliest
      key-deserializer: org.apache.kafka.common.serialization.StringDeserializer
      value-deserializer: org.springframework.kafka.support.serializer.JsonDeserializer
      properties:
        spring.json.trusted.packages: "*"
        spring.json.value.default.type: com.fayupable.mailsender.kafka.user.UserConfirmation
        spring.json.type.mapping: userConfirmation:com.fayupable.mailsender.kafka.user.UserConfirmation
  mail:
    host: localhost
    port: 1025
    username: fayupable
    password: fayupable
    properties:
      mail:
        smtp:
          trust: "*"
        auth: true
        starttls:
          enabled: true
        connectiontimeout: 5000
        timeout: 3000
        writetimeout: 5000
```

```yaml
spring:
  config:
    import: optional:configserver:http://localhost:8888
  application:
    name: notification-service
```

### Running the Service

1. Clone the repository:
   ```sh
   git clone https://github.com/your-org/notification-service.git
   cd notification-service
   ```
2. Build the project:
   ```sh
   mvn clean package
   ```
3. Run the application:
   ```sh
   mvn spring-boot:run
   ```

### Testing the Service

You can send a test notification using the REST API:
```sh
curl -X POST "http://localhost:8083/api/notifications" \
     -H "Content-Type: application/json" \
     -d '{"recipient": "user@example.com", "message": "Your transaction was successful"}'
```

## Kafka Integration

To verify Kafka integration, use the following command to publish a test event:
```
echo '{"recipient": "user@example.com", "message": "Test Kafka Notification"}' |
kafka-console-producer --broker-list localhost:9092 --topic notification-events
```

## Kafka Consumer Log
``` markdown
2025-02-11T15:02:13.083+03:00  INFO 15913 --- [user-service] [nio-8070-exec-4] c.f.mailsender.kafka.UserProducer        : Sending confirmation: UserConfirmation(userId=65045ce9-4b54-4389-b574-d935b9b02fdb, email=johndo3e@ex34ample2.com, verificationCode=cd943fe27c, verificationCodeExpiration=2025-02-11T15:07:13.059396, userLoginTime=null)
```
```json
{
  "userId": "65045ce9-4b54-4389-b574-d935b9b02fdb",
  "email": "johndo3e@ex34ample2.com",
  "verificationCode": "cd943fe27c",
  "verificationCodeExpiration": "2025-02-11T15:07:13.059396",
  "userLoginTime": null
}
```
![Email Template](https://github.com/Fayupable/KafkaMailSenderMicroservices/blob/main/services/notification/images/emailverification.jpeg)


```markdown
2025-02-11T15:01:40.818+03:00  INFO 16718 --- [notification-service] [ntainer#1-0-C-1] c.f.m.kafka.NotificationConsumer         : Consuming user login message: UserConfirmation(userId=d1c4d737-3a05-41be-940f-f859de08da73, email=johndoe@ex34ample2.com, verificationCode=null, verificationCodeExpiration=null, userLoginTime=2025-02-11T15:01:40)
```

```json
{
  "userId": "d1c4d737-3a05-41be-940f-f859de08da73",
  "email": "johndoe@ex34ample2.com",
  "verificationCode": null,
  "verificationCodeExpiration": null,
  "userLoginTime": "2025-02-11T15:01:40"
}
```

![Email Template](https://github.com/Fayupable/KafkaMailSenderMicroservices/blob/main/services/notification/images/userlogin.jpeg)




## Docker Support

To run the **Notification Service** inside a Docker container:

1. Create a `Dockerfile`:
   ```Dockerfile
   FROM openjdk:21-jdk
   COPY target/notification-service-0.0.1-SNAPSHOT.jar app.jar
   ENTRYPOINT ["java", "-jar", "app.jar"]
   ```
2. Build and run the container:
   ```sh
   docker build -t notification-service .
   docker run -p 8083:8083 notification-service
   ```

## Common Issues & Troubleshooting

### 1. Kafka Connection Issues

**Error:** `Connection to Kafka broker failed`
**Solution:** Ensure Kafka is running and correctly configured in `application.yml`.

### 2. Email Not Sending

**Error:** `Mail sending failed`
**Solution:** Verify SMTP credentials and server configuration.

### 3. Database Connection Issues

**Error:** `Failed to connect to PostgreSQL`
**Solution:** Ensure PostgreSQL is running and credentials are correct in `application.yml`.

## Conclusion

The **Notification Service** plays a crucial role in event-driven architectures, enabling real-time notifications for users. With **Kafka**, **Spring Mail**, and **PostgreSQL**, it ensures efficient and reliable message delivery.

