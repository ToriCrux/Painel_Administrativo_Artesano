# ===== builder =====
FROM eclipse-temurin:21-jdk AS builder
WORKDIR /app

# Copia os arquivos do projeto para o container
COPY . .

# Dá permissão de execução ao Maven Wrapper
RUN chmod +x ./mvnw

# Faz o build sem executar os testes (para pipelines de CI/CD)
RUN ./mvnw -q -DskipTests package

# ===== runtime =====
FROM eclipse-temurin:21-jre
WORKDIR /app

# Define configurações de memória da JVM
ENV JAVA_OPTS="-Xms256m -Xmx384m"

# Copia o .jar gerado na etapa anterior
COPY --from=builder /app/target/*.jar /app/app.jar

# Expõe a porta que o serviço usa
EXPOSE 8082

# Adiciona healthcheck (para ambientes Docker Compose ou Kubernetes)
HEALTHCHECK --interval=10s --timeout=3s --retries=30 --start-period=30s \
  CMD wget -qO- http://localhost:8082/actuator/health | grep -q '"status":"UP"' || exit 1

# Comando padrão de inicialização
CMD ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]
