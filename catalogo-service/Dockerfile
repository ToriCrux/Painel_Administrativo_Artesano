# ===== BUILD STAGE =====
FROM eclipse-temurin:21-jdk AS builder
WORKDIR /app

# Copia Maven wrapper e POM primeiro (para cache)
COPY mvnw pom.xml ./
COPY .mvn .mvn

# Baixa dependências (cache)
RUN ./mvnw dependency:go-offline -B

# Copia o código-fonte
COPY src src

# Gera o JAR sem testes
RUN ./mvnw clean package -Dmaven.test.skip=true -B

# ===== RUNTIME STAGE =====
FROM eclipse-temurin:21-jre
WORKDIR /app

# Copia o JAR gerado com nome fixo
COPY --from=builder /app/target/*.jar /app/app.jar

# Configura ambiente
ENV JAVA_OPTS="-Xms256m -Xmx384m"
ENV TZ=America/Sao_Paulo
ENV SPRING_PROFILES_ACTIVE=docker

# Cria usuário não-root
RUN useradd -ms /bin/bash appuser
USER appuser

# Expõe a porta da aplicação
EXPOSE 8082

# Healthcheck via actuator
HEALTHCHECK --interval=10s --timeout=3s --retries=10 --start-period=20s \
  CMD wget -qO- http://localhost:8082/actuator/health | grep -q '"status":"UP"' || exit 1

# Executa a aplicação
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]
