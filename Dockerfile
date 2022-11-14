FROM maven:3-openjdk-17 as build

COPY pom.xml .
RUN mvn dependency:go-offline
RUN mvn dependency:copy-dependencies

COPY src src
RUN mvn test

FROM amazoncorretto:17

RUN mkdir -p /app/lib
COPY --from=build target/dependency/* /app/lib/
COPY --from=build target/classes /app/classes
COPY --from=build src/main/azure/* /app/

EXPOSE 8080
WORKDIR /app
CMD ["java", "-classpath", "/app/classes:/app/lib/*", "com.soprasteria.workshop.openapi.PetStoreServer", "start"]

