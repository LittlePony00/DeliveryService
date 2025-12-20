FROM gradle:8.13-jdk17 AS builder

WORKDIR /app
ENV GRADLE_USER_HOME=/home/gradle/.gradle

COPY gradlew .
COPY gradle gradle
COPY build.gradle.kts settings.gradle.kts ./
COPY audit-service audit-service
COPY events-contract events-contract
COPY api api
COPY analytics-service analytics-service
COPY main main
COPY statistics-service statistics-service

RUN --mount=type=cache,target=${GRADLE_USER_HOME} gradle :main:bootJar --no-daemon

FROM eclipse-temurin:17-jre-alpine

WORKDIR /app
RUN addgroup -S javauser && adduser -S -G javauser javauser
USER javauser

COPY --from=builder /app/main/build/libs/*.jar app.jar

EXPOSE 25566
ENTRYPOINT ["java", "-jar", "app.jar"]
