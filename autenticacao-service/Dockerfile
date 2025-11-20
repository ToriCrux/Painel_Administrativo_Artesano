# ===== builder =====
FROM eclipse-temurin:21-jdk AS builder
WORKDIR /app
COPY . .
RUN ./mvnw -q -DskipTests package

# ===== runtime =====
FROM eclipse-temurin:21-jre
WORKDIR /app
ENV JAVA_OPTS="-Xms256m -Xmx384m"
COPY --from=builder /app/target/*.jar /app/app.jar
EXPOSE 8081
HEALTHCHECK --interval=10s --timeout=3s --retries=30 --start-period=30s \
  CMD wget -qO- http://localhost:8081/actuator/health | grep -q '"status":"UP"'
CMD ["sh","-c","java $JAVA_OPTS -jar /app/app.jar"]
