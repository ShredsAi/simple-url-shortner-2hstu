# Multi-stage build for optimized image size and security
FROM maven:3.9-eclipse-temurin-17 AS build

# Create app directory and set working directory
WORKDIR /app

# Copy dependency files first for better layer caching
COPY pom.xml ./
COPY .mvn/ .mvn/

# Download dependencies (this layer will be cached unless pom.xml changes)
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Build the application
RUN mvn clean package -DskipTests -B

# Runtime stage
FROM eclipse-temurin:17-jre-alpine AS runtime

# Install required packages and create non-root user
RUN apk add --no-cache \
    curl \
    wget \
    && addgroup -g 1000 appgroup \
    && adduser -u 1000 -G appgroup -s /bin/sh -D appuser

# Create app directory
WORKDIR /app

# Copy built application from build stage
COPY --from=build /app/target/url-management-shred.jar app.jar

# Change ownership to non-root user
RUN chown -R appuser:appgroup /app

# Switch to non-root user
USER appuser:appgroup

# Expose port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

# JVM optimization and security flags
ENV JAVA_OPTS="\
    -Xms256m \
    -Xmx512m \
    -XX:+UseG1GC \
    -XX:+UseStringDeduplication \
    -XX:+OptimizeStringConcat \
    -Djava.security.egd=file:/dev/./urandom \
    -Dspring.profiles.active=prod \
    -Dfile.encoding=UTF-8 \
    -Duser.timezone=UTC"

# Application entry point
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]

# Labels for metadata
LABEL maintainer="AI Shreds" \
      description="URL Management Shred - Microservice for managing shortened URLs" \
      version="1.0.0-SNAPSHOT" \
      java.version="17" \
      spring.boot.version="3.2.0"
