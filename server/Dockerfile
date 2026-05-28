# Stage 1: Build
FROM gradle:8.11-jdk21 AS builder

WORKDIR /app

# Copy build files first for layer caching
COPY gradle/ gradle/
COPY gradlew gradlew.bat ./
COPY build.gradle.kts settings.gradle.kts gradle.properties ./
COPY gradle/libs.versions.toml gradle/libs.versions.toml
COPY server/build.gradle.kts server/build.gradle.kts
COPY composeApp/build.gradle.kts composeApp/build.gradle.kts
COPY androidApp/build.gradle.kts androidApp/build.gradle.kts

# Download dependencies
RUN ./gradlew dependencies --no-daemon || true

# Copy source code
COPY server/src/ server/src/
COPY composeApp/src/ composeApp/src/
COPY androidApp/src/ androidApp/src/

# Build server distribution
RUN ./gradlew :server:installDist --no-daemon

# Stage 2: Runtime
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Install curl for healthcheck
RUN apk add --no-cache curl

# Copy the distribution from builder
COPY --from=builder /app/server/build/install/server/ .

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=5s --start-period=10s --retries=3 \
    CMD curl -f http://localhost:8080/health || exit 1

ENTRYPOINT ["./bin/server"]
