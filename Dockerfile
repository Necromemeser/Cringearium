FROM openjdk:23-jdk

WORKDIR /app

# Копируем файлы проекта
COPY pom.xml .
COPY src ./src

COPY target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]