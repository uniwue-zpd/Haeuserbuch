FROM maven:3.9-eclipse-temurin-21-alpine AS build

WORKDIR /app

COPY . .

RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jdk-alpine

WORKDIR /app

COPY --from=build /app/target/haeuserbuch_backend-0.0.1-SNAPSHOT.jar /app/app.jar

EXPOSE 8080

CMD ["java", "-jar", "/app/app.jar"]
