FROM openjdk:23-jdk AS builder

WORKDIR /app
COPY . .

# Собираем проект (используйте вашу команду сборки)
RUN ./mvnw clean package

# Финальный образ
FROM openjdk:23-jdk
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]