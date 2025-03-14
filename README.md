# Challenge Application

This project is a sample Spring Boot application with exception handling, logging, and caching using Caffeine. It is designed to showcase how to manage exceptions globally and log API calls using Spring AOP.

## Overview

- **Global Exception Handling:**  
  A `GlobalExceptionHandler` (annotated with `@RestControllerAdvice`) captures and processes exceptions thrown during REST API calls. It handles specific exceptions (e.g., `DatabaseConnectionException`, `PercentageUnavailableException`) and a general catch-all for any other unexpected exceptions.

- **Logging Aspect:**  
  The `LoggingAspect` intercepts controller methods (specifically those annotated with `@PostMapping`) to log details such as the endpoint, input parameters, and responses. This information is recorded via a service (`ICallLogService`) that persists the logs on database.

- **Caching with Caffeine:**  
  The `PercentageService` fetches a percentage value from an external service and caches it using Caffeine. The cache stores the value for 30 minutes, and if the external service fails, it returns the last cached value or throws an exception if no value is available.

## Project Structure

```plaintext
src/
  main/
    java/
      com.github.luishdezortega.challenge/
        config
        controller
        dto
        exception
        integration
        model
        repository
        service
        util
  test/
    java/
      ...                              # Unit and integration tests
```
## Prerequisites

- **Docker:** Ensure Docker is installed.
- **Docker Compose:** Install Docker Compose.
- **Docker Hub Account:** Verify you have access to Docker Hub (login with `docker login` if required).


## Steps to Run the Application
To run the application using the pre-built Docker image, follow these steps:

1. **Create a Docker network** to allow communication between containers:
   ```bash
   docker network create challenge_network
   ```

2. **Run the PostgreSQL database container**:
   ```bash
   docker run -d --name postgres_db --network challenge_network -p 5432:5432 \
     -e POSTGRES_USER=admin \
     -e POSTGRES_PASSWORD=admin \
     -e POSTGRES_DB=CHALLENGE_DATABASE \
     postgres:latest
   ```

3. **Run the Spring Boot application container**, linking it to the database:
   ```bash
   docker run -d --name spring_app --network challenge_network -p 8080:8080 \
     -e SPRING_DATASOURCE_URL=jdbc:postgresql://postgres_db:5432/CHALLENGE_DATABASE \
     -e SPRING_DATASOURCE_USERNAME=admin \
     -e SPRING_DATASOURCE_PASSWORD=admin \
     -e SPRING_JPA_HIBERNATE_DDL_AUTO=update \
     luis231995/challenge-application:latest
   ```

Now the application should be running and accessible at `http://localhost:8080`.

## Testing the API

You can test the API using the following `curl` commands:

### Get Percentage
```sh
  curl --location 'localhost:8080/api/percentage' \
--header 'Content-Type: application/json' \
--data '{
    "numberOne" : 3,
    "numberTwo" : 4
}'
```

### Get Logs
```sh
  curl --location 'localhost:8080/api/logs?size=2&sort=asc'
```
## Challenges Encountered

- **Logging Aspect and Global Exception:**  
  At one point, we discovered that with aspect-oriented Spring we could configure the pointcut to also capture error responses. However, since we also have centralized exception handling with `@RestControllerAdvice`, we found that no error responses were being stored. Therefore, we opted to store only the successful responses via the logging aspect and let the global exception handler manage everything.

- **Cache with Caffeine:**  
  Initially, we started with cache configuration using properties and Spring annotations like `@Cacheable`, but later on this created issues when we attempted to review the cache in specific cases. This led to circular references and additional code for verification. Although adjustments could have been made, I felt it was more appropriate to switch to a manual implementation, which simplified the process.
