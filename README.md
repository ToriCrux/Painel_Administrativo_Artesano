# Painel Administrativo

Painel administrativo da fábrica de ladrilhos (projeto da faculdade). Esta versão entrega o projeto base, com Flyway e ambiente dev com H2.

### 📦 Stack
- Java 21 • Spring Boot 3.5.4
- Web, Data JPA, Validation, Security
- Flyway (migrations)
- H2 (dev) • PostgreSQL (prod)
- Springdoc OpenAPI (Swagger UI)

### ✅ Requisitos
- Java 21+
- Maven 3.9+

### 🚀 Como executar

Após acessar o projeto, no terminal da IDE que estiver utilizando, acesse a pasta raiz do projeto (mesma pasta onde está o arquivo pom.xlm), e execute o comando:

```
mvn spring-boot:run "-Dspring-boot.run.profiles=dev"
```

A aplicação sobe em: http://localhost:8080

E alguns endpoints úteis são:
- Swagger UI: http://localhost:8080/swagger-ui.html
- H2 Console: http://localhost:8080/h2-console
    - No H2 console atenção as configurações: <br>
        **_JDBC URL:_** jdbc:h2:mem:ladrilhos <br>
        **_User:_** sa <br>
        **_Password:_** (vazio)