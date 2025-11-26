# ===== builder =====
FROM eclipse-temurin:21-jdk AS builder
WORKDIR /app

# Copia os arquivos do projeto
COPY . .

# Corrige permissões do Maven Wrapper
RUN chmod +x ./mvnw

# Faz o build sem executar testes (para pipelines CI/CD)
RUN ./mvnw -q -DskipTests package

# ===== runtime =====
FROM eclipse-temurin:21-jre
WORKDIR /app

# Configura memória do serviço
ENV JAVA_OPTS="-Xms128m -Xmx256m"

# Copia o artefato gerado do estágio anterior
COPY --from=builder /app/target/*.jar /app/app.jar

# Expõe a porta do serviço de registro (Eureka)
EXPOSE 8761

# Healthcheck: verifica se o Eureka está UP
HEALTHCHECK --interval=10s --timeout=3s --retries=20 --start-period=20s \
  CMD wget -qO- http://localhost:8761/actuator/health | grep -q '"status":"UP"' || exit 1

# Comando padrão para iniciar o serviço
CMD ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]
