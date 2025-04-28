# Этап сборки (builder)
FROM maven:3.9.9-eclipse-temurin-21 AS builder

WORKDIR /app
COPY pom.xml .
# Копируем исходный код
COPY src ./src

# Собираем приложение
RUN mvn clean package -DskipTests

# Финальный этап
FROM eclipse-temurin:21-jre-jammy

WORKDIR /app
# Копируем собранный JAR из этапа builder
COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]