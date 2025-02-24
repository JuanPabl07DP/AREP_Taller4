FROM maven:3.8.1-openjdk-17-slim AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

FROM openjdk:17-slim
WORKDIR /app
COPY --from=build /app/target/currency-converter-1.0-SNAPSHOT-jar-with-dependencies.jar app.jar
COPY src/main/resources/public /app/public
RUN ls -la /app/public
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]