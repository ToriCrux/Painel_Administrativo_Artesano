# ===== builder =====
FROM eclipse-temurin:21-jdk AS builder
WORKDIR /app

# Copia todo o código do projeto para o container
COPY . .

# Dá permissão de execução ao Maven Wrapper (evita "Permission denied")
RUN chmod +x ./mvnw

# Faz o build sem rodar os testes (mais rápido para CI/CD)
RUN ./mvnw -q -DskipTests package

# ===== runtime =====
FROM eclipse-temurin:21-jre
WORKDIR /app

# Define limites de memória da JVM (ajuste conforme o deploy)
ENV JAVA_OPTS="-Xms256m -Xmx384m"

# Copia o artefato gerado no estágio anterior
COPY --from=builder /app/target/*.jar /app/app.jar

# Expõe a porta usada por este microserviço
EXPOSE 8081

# Healthcheck — verifica se o serviço está de pé
HEALTHCHECK --interval=10s --timeout=3s --retries=30 --start-period=30s \
  CMD wget -qO- http://localhost:8081/actuator/health | grep -q '"status":"UP"' || exit 1

# Comando padrão de inicialização
CMD ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]
