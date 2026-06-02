FROM maven:3.9.16-amazoncorretto-21 AS build

WORKDIR /app

COPY . .

RUN mvn clean package -DskipTests

FROM gradle:jdk21-corretto

WORKDIR /app

COPY --from=build /app/target/haeuserbuch_backend-0.0.1-SNAPSHOT.jar /app/app.jar

EXPOSE 8080

CMD ["java", "-jar", "/app/app.jar"]
