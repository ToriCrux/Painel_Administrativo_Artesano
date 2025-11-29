# ===== builder =====
FROM eclipse-temurin:21-jdk AS builder
WORKDIR /app
COPY . .
RUN ./mvnw -q -DskipTests package

# ===== runtime =====
FROM eclipse-temurin:21-jre
WORKDIR /app
ENV JAVA_OPTS="-Xms128m -Xmx256m"
COPY --from=builder /app/target/*.jar /app/app.jar
EXPOSE 8080
HEALTHCHECK --interval=10s --timeout=3s --retries=20 --start-period=20s \
  CMD wget -qO- http://localhost:8080/actuator/health | grep -q '"status":"UP"'
CMD ["sh","-c","java $JAVA_OPTS -jar /app/app.jar"]
