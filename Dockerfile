# Stage 1: Build the application
FROM maven:3.9.6-eclipse-temurin-21-alpine AS build

# Set the current working directory inside the image
WORKDIR /app

# Copy the pom.xml file to download dependencies
COPY pom.xml .

# Download the dependencies (this layer is cached unless pom.xml changes)
RUN mvn dependency:go-offline -B

# Copy the actual source code
COPY src ./src

# Package the application (skip tests for faster builds, you can change this later)
RUN mvn clean package -DskipTests

# Stage 2: Create the final lightweight runtime image
FROM eclipse-temurin:21-jre-alpine

# Set the working directory
WORKDIR /app

# Copy the generated JAR file from the build stage
# Note: The jar name 'ecommerce-0.0.1-SNAPSHOT.jar' comes from your pom.xml (artifactId and version)
COPY --from=build /app/target/ecommerce-0.0.1-SNAPSHOT.jar app.jar

# Expose port 8080
EXPOSE 8080

# Command to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
