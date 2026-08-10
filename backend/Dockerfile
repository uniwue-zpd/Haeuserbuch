FROM maven:3.9.16-amazoncorretto-21

LABEL description="Development image for the Haeuserbuch Spring Boot backend"

WORKDIR /app

COPY pom.xml ./
RUN mvn --batch-mode dependency:go-offline -DskipTests

COPY src ./src
COPY docker/dev-entrypoint.sh /usr/local/bin/haeuserbuch-dev
RUN chmod 755 /usr/local/bin/haeuserbuch-dev

EXPOSE 8080

ENTRYPOINT ["/usr/local/bin/haeuserbuch-dev"]
