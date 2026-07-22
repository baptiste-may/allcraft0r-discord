# ── Stage 1: Build ─────────────────────────────────────────────────────────────
FROM eclipse-temurin:25-jdk AS builder
WORKDIR /app

# Copy gradle wrapper and configuration files first for caching
COPY gradle/ gradle/
COPY gradlew build.gradle settings.gradle ./
RUN chmod +x ./gradlew

# Pre-fetch Gradle dependencies
RUN ./gradlew dependencies --no-daemon

# Copy source code and build bootable JAR
COPY src/ src/
RUN ./gradlew bootJar --no-daemon -x test

# ── Stage 2: Runtime ───────────────────────────────────────────────────────────
FROM eclipse-temurin:25-jdk AS runner
WORKDIR /app

# Run as non-root user for security
RUN groupadd -r appgroup && useradd -r -g appgroup appuser

COPY --from=builder /app/build/libs/*.jar app.jar
RUN chown appuser:appgroup app.jar

USER appuser

EXPOSE 8080

ENV PORT=8080

# Production JVM optimizations for containers
ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]
