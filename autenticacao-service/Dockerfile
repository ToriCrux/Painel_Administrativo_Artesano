# ==========================
# ===== BUILD STAGE ========
# ==========================
FROM eclipse-temurin:21-jdk AS builder
WORKDIR /app

# Copia Maven Wrapper e POM primeiro (para cache eficiente)
COPY mvnw pom.xml ./
COPY .mvn .mvn

# Baixa dependências Maven (sem compilar testes)
RUN ./mvnw dependency:go-offline -B -Dmaven.test.skip=true

# Copia o restante do código-fonte
COPY src src

# Compila o projeto (sem compilar nem rodar testes)
RUN ./mvnw clean package -B -Dmaven.test.skip=true


# ==========================
# ===== RUNTIME STAGE ======
# ==========================
FROM eclipse-temurin:21-jre
WORKDIR /app

# Copia o artefato gerado
COPY --from=builder /app/target/*.jar /app/app.jar

# Configurações de ambiente
ENV TZ=America/Sao_Paulo
ENV SPRING_PROFILES_ACTIVE=docker
ENV JAVA_OPTS="-Xms256m -Xmx384m -Dfile.encoding=UTF-8"

# Observabilidade e logging
ENV MANAGEMENT_TRACING_SAMPLING_PROBABILITY="1.0"
ENV MANAGEMENT_ZIPKIN_TRACING_ENDPOINT="http://zipkin:9411/api/v2/spans"
ENV MANAGEMENT_ENDPOINTS_WEB_EXPOSURE_INCLUDE="health,info,prometheus"
ENV LOGGING_FILE_NAME="/var/log/app/app.log"
ENV LOGGING_PATTERN_LEVEL="%5p [%X{traceId:-},span=%X{spanId:-}]"

# Cria diretório de logs e usuário não-root
RUN mkdir -p /var/log/app && \
    useradd -ms /bin/bash appuser && \
    chown -R appuser:appuser /var/log/app

# Troca para o usuário seguro
USER appuser

# Porta — personalize conforme o microserviço
EXPOSE 8081

# Healthcheck
HEALTHCHECK --interval=10s --timeout=5s --retries=10 --start-period=20s \
  CMD wget -qO- http://localhost:8081/actuator/health | grep -q '"status":"UP"' || exit 1

# Inicializa a aplicação
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]
