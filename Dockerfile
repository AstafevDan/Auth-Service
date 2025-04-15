FROM eclipse-temurin:17-jdk-alpine AS builder

WORKDIR /app

COPY build.gradle gradlew gradlew.bat settings.gradle ./
COPY gradle ./gradle/
COPY src ./src/

RUN chmod +x gradlew
RUN ./gradlew bootJar

FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]