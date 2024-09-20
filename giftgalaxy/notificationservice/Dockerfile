# Build the application using Maven
FROM maven:3.9.8-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean install -DskipTests

#Use a smaller JDK image to run the application
FROM eclipse-temurin:21.0.4_7-jdk-alpine

# Set the working directory in the container
WORKDIR /app

# Copy the jar file generated from the Maven build
COPY --from=build /app/target/notificationservice-0.0.1-SNAPSHOT.jar app.jar

# Expose the port the notification service will run on
EXPOSE 8082

# Command to run the application
ENTRYPOINT [ "java", "-jar", "app.jar" ]
