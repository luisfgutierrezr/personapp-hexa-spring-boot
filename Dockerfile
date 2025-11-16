# Multi-stage build for Spring Boot application
# Stage 1: Build stage
FROM maven:3.9-eclipse-temurin-11 AS build
WORKDIR /app

# Copy pom files
COPY pom.xml .
COPY */pom.xml ./

# Download dependencies (this layer will be cached if pom.xml doesn't change)
RUN mvn dependency:go-offline -B || true

# Copy source code
COPY . .

# Build the application (excluding CLI adapter as it's not needed for REST API)
RUN mvn clean package -DskipTests -pl rest-input-adapter -am

# Stage 2: Runtime stage
FROM eclipse-temurin:11-jre-alpine
WORKDIR /app

# Create logs directory
RUN mkdir -p logs

# Copy the JAR from build stage
COPY --from=build /app/rest-input-adapter/target/rest-input-adapter-*.jar app.jar

# Expose port 3000
EXPOSE 3000

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]

