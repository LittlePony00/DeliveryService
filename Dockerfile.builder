# Общий builder для всех сервисов - собирается один раз!
FROM gradle:8.13-jdk17 AS builder

WORKDIR /app

# Копируем gradle wrapper и конфиги
COPY gradlew .
COPY gradle gradle
COPY build.gradle.kts settings.gradle.kts gradle.properties ./

# Копируем все исходники
COPY api api
COPY events-contract events-contract
COPY analytics-service analytics-service
COPY audit-service audit-service
COPY statistics-service statistics-service
COPY main main

# Собираем ВСЕ сервисы параллельно за один раз!
RUN gradle clean \
    :analytics-service:bootJar \
    :audit-service:bootJar \
    :statistics-service:bootJar \
    :main:bootJar \
    --parallel \
    --no-daemon \
    --build-cache \
    -x test

# Проверяем, что JAR файлы созданы
RUN ls -la analytics-service/build/libs/ && \
    ls -la audit-service/build/libs/ && \
    ls -la statistics-service/build/libs/ && \
    ls -la main/build/libs/


