
FROM eclipse-temurin:21-jdk AS builder
WORKDIR /app

COPY pom.xml mvnw ./
COPY .mvn .mvn
RUN ./mvnw dependency:go-offline -B

COPY src src
RUN ./mvnw -q -DskipTests package

FROM eclipse-temurin:21-jre
WORKDIR /app

ENV JAVA_OPTS="-Xms256m -Xmx384m -Dfile.encoding=UTF-8"

COPY --from=builder /app/target/*.jar /app/app.jar

EXPOSE 8081

HEALTHCHECK --interval=10s --timeout=3s --retries=30 --start-period=30s \
  CMD curl -f http://localhost:8081/actuator/health || exit 1

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]
