# Используем общий builder
FROM gradle:8.13-jdk17 AS builder

WORKDIR /app

COPY gradlew .
COPY gradle gradle
COPY build.gradle.kts settings.gradle.kts gradle.properties ./
COPY api api
COPY events-contract events-contract
COPY analytics-service analytics-service
COPY audit-service audit-service
COPY statistics-service statistics-service
COPY main main

# Собираем только analytics-service (или все параллельно)
RUN gradle :analytics-service:bootJar --parallel --no-daemon --build-cache -x test

FROM eclipse-temurin:17-jre-alpine

WORKDIR /app
RUN addgroup -S javauser && adduser -S -G javauser javauser
USER javauser

COPY --from=builder /app/analytics-service/build/libs/*.jar app.jar

EXPOSE 25590
ENTRYPOINT ["java", "-jar", "app.jar"]
