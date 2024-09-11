# Maven image to build the project
FROM maven:3.9.8-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean install -DskipTests

#usage of Eclipse Temurin JDK 21 (21.0.4) as image 
FROM eclipse-temurin:21.0.4_7-jdk-alpine

#set of the working directory in the container
WORKDIR /app

# .jar file generated from the Maven build 
COPY --from=build /app/target/authenticationservice-0.0.1-SNAPSHOT.jar app.jar
#COPY target/authenticationservice-0.0.1-SNAPSHOT.jar app.jar

#port of the spring boot
EXPOSE 8080

#command to start the application
ENTRYPOINT [ "java", "-jar", "app.jar" ]