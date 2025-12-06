# Painel Administrativo Artesano — Kubernetes (kind) Setup (Windows)

Este guia sobe o projeto **Painel_Administrativo_Artesano** em um cluster **Kubernetes local** usando **kind** + **Helm**.

Arquitetura (alto nível):
- `registro-service` (Eureka)
- `api-gateway`
- `autenticacao-service`
- `catalogo-service`
- `estoque-service`
- `proposta-service`
- Infra: 4x Postgres, MongoDB, RabbitMQ, Zipkin

---

## 0) Pré-requisitos (Windows)

### 0.1 Docker Desktop
- Instale e deixe o **Docker Desktop rodando**.

### 0.2 Ferramentas (PowerShell)
Instale com `winget`:

```powershell
winget install -e --id Kubernetes.kubectl
winget install -e --id Kubernetes.kind
winget install -e --id Helm.Helm
```

Valide:

```powershell
kubectl version --client
kind version
helm version
```

> Se `kind` não for reconhecido: feche e reabra o PowerShell. Persistindo, use o FAQ no final.

---

## 1) Criar cluster Kubernetes (kind)

```powershell
kind create cluster --name artesano
kubectl get nodes
```

Crie o namespace:

```powershell
kubectl create namespace artesano
kubectl get ns
```

---

## 2) Subir dependências com Helm (Postgres/Mongo/RabbitMQ)

Adicionar repositório Bitnami:

```powershell
helm repo add bitnami https://charts.bitnami.com/bitnami
helm repo update
```

### 2.1 Postgres (4 bancos, 1 por serviço)

Auth:
```powershell
helm install postgres-auth bitnami/postgresql -n artesano `
  --set fullnameOverride=postgres-auth `
  --set auth.database=auth_db `
  --set auth.username=auth_user `
  --set auth.password=auth_pass `
  --set primary.persistence.size=2Gi
```

Catálogo:
```powershell
helm install postgres-catalogo bitnami/postgresql -n artesano `
  --set fullnameOverride=postgres-catalogo `
  --set auth.database=catalogo_db `
  --set auth.username=catalogo_user `
  --set auth.password=catalogo_pass `
  --set primary.persistence.size=2Gi
```

Estoque:
```powershell
helm install postgres-estoque bitnami/postgresql -n artesano `
  --set fullnameOverride=postgres-estoque `
  --set auth.database=estoque_db `
  --set auth.username=estoque_user `
  --set auth.password=estoque_pass `
  --set primary.persistence.size=2Gi
```

Proposta:
```powershell
helm install postgres-proposta bitnami/postgresql -n artesano `
  --set fullnameOverride=postgres-proposta `
  --set auth.database=proposta_db `
  --set auth.username=proposta_user `
  --set auth.password=proposta_pass `
  --set primary.persistence.size=2Gi
```

### 2.2 Mongo (dev, sem senha)
```powershell
helm install mongo bitnami/mongodb -n artesano `
  --set fullnameOverride=mongo `
  --set auth.enabled=false `
  --set persistence.size=2Gi
```

### 2.3 RabbitMQ
```powershell
helm install rabbitmq bitnami/rabbitmq -n artesano `
  --set fullnameOverride=rabbitmq `
  --set auth.username=guest `
  --set auth.password=guest `
  --set persistence.size=2Gi
```

---

## 3) Zipkin (YAML)

Crie a pasta `k8s` na raiz do projeto:

```
/k8s
```

Crie o arquivo:

### `k8s/zipkin.yaml`
```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: zipkin
  namespace: artesano
spec:
  replicas: 1
  selector:
    matchLabels: { app: zipkin }
  template:
    metadata:
      labels: { app: zipkin }
    spec:
      containers:
        - name: zipkin
          image: openzipkin/zipkin:latest
          ports:
            - containerPort: 9411
---
apiVersion: v1
kind: Service
metadata:
  name: zipkin
  namespace: artesano
spec:
  selector: { app: zipkin }
  ports:
    - name: http
      port: 9411
      targetPort: 9411
```

Aplique:

```powershell
kubectl apply -f k8s/zipkin.yaml
```

Confira infra:

```powershell
kubectl -n artesano get pods
kubectl -n artesano get svc
```

---

## 4) Build das imagens Docker (local)

Na raiz do projeto, build das imagens:

```powershell
docker build -t artesano/registro-service:0.1.0 ./registro-service
docker build -t artesano/api-gateway:0.1.0      ./api-gateway
docker build -t artesano/autenticacao:0.1.0     ./autenticacao-service
docker build -t artesano/catalogo:0.1.0         ./catalogo-service
docker build -t artesano/estoque:0.1.0          ./estoque-service
docker build -t artesano/proposta:0.1.0         ./proposta-service
```

Carregue as imagens no cluster kind:

```powershell
kind load docker-image artesano/registro-service:0.1.0 --name artesano
kind load docker-image artesano/api-gateway:0.1.0      --name artesano
kind load docker-image artesano/autenticacao:0.1.0     --name artesano
kind load docker-image artesano/catalogo:0.1.0         --name artesano
kind load docker-image artesano/estoque:0.1.0          --name artesano
kind load docker-image artesano/proposta:0.1.0         --name artesano
```

---

## 5) Configuração comum (Secret + ConfigMap)

Crie o arquivo:

### `k8s/00-config.yaml`
```yaml
apiVersion: v1
kind: Secret
metadata:
  name: artesano-secrets
  namespace: artesano
type: Opaque
stringData:
  # Pegue do seu .env do docker-compose (exemplo abaixo)
  AUTH_JWT_SECRET_BASE64: "kKm5G2MIj9aMOK/y6v0tXpLHmv8nciqTmrLb+HlJk3k="
---
apiVersion: v1
kind: ConfigMap
metadata:
  name: artesano-config
  namespace: artesano
data:
  EUREKA_CLIENT_SERVICEURL_DEFAULTZONE: "http://registry:8761/eureka/"
  EUREKA_INSTANCE_PREFER_IP_ADDRESS: "true"

  MANAGEMENT_TRACING_SAMPLING_PROBABILITY: "1.0"
  MANAGEMENT_ZIPKIN_TRACING_ENDPOINT: "http://zipkin:9411/api/v2/spans"
  MANAGEMENT_ENDPOINTS_WEB_EXPOSURE_INCLUDE: "health,info,prometheus"

  SPRING_RABBITMQ_HOST: "rabbitmq"
  SPRING_RABBITMQ_PORT: "5672"
  SPRING_RABBITMQ_USERNAME: "guest"
  SPRING_RABBITMQ_PASSWORD: "guest"
```

Aplique:

```powershell
kubectl apply -f k8s/00-config.yaml
```

---

## 6) Deploy dos serviços (Eureka + MS + Gateway)

Crie o arquivo:

### `k8s/10-apps.yaml`
> **Observação:** assume que cada serviço expõe `actuator/health` e aceita `SPRING_PROFILES_ACTIVE=docker`.
```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: registry
  namespace: artesano
spec:
  replicas: 1
  selector:
    matchLabels: { app: registry }
  template:
    metadata:
      labels: { app: registry }
    spec:
      containers:
        - name: registry
          image: artesano/registro-service:0.1.0
          imagePullPolicy: IfNotPresent
          ports: [{ containerPort: 8761 }]
          env:
            - { name: SERVER_PORT, value: "8761" }
            - { name: SPRING_PROFILES_ACTIVE, value: "docker" }
          readinessProbe:
            httpGet: { path: /actuator/health, port: 8761 }
            initialDelaySeconds: 20
            periodSeconds: 10
---
apiVersion: v1
kind: Service
metadata:
  name: registry
  namespace: artesano
spec:
  selector: { app: registry }
  ports:
    - name: http
      port: 8761
      targetPort: 8761
---
apiVersion: apps/v1
kind: Deployment
metadata:
  name: autenticacao-service
  namespace: artesano
spec:
  replicas: 1
  selector:
    matchLabels: { app: autenticacao-service }
  template:
    metadata:
      labels: { app: autenticacao-service }
    spec:
      containers:
        - name: autenticacao
          image: artesano/autenticacao:0.1.0
          imagePullPolicy: IfNotPresent
          ports: [{ containerPort: 8081 }]
          envFrom:
            - configMapRef: { name: artesano-config }
            - secretRef: { name: artesano-secrets }
          env:
            - { name: SERVER_PORT, value: "8081" }
            - { name: SPRING_PROFILES_ACTIVE, value: "docker" }
            - { name: SPRING_APPLICATION_NAME, value: "AUTENTICACAO-SERVICE" }

            - { name: SPRING_DATASOURCE_URL, value: "jdbc:postgresql://postgres-auth:5432/auth_db" }
            - { name: SPRING_DATASOURCE_USERNAME, value: "auth_user" }
            - { name: SPRING_DATASOURCE_PASSWORD, value: "auth_pass" }
            - { name: SPRING_FLYWAY_URL, value: "jdbc:postgresql://postgres-auth:5432/auth_db" }
            - { name: SPRING_FLYWAY_USER, value: "auth_user" }
            - { name: SPRING_FLYWAY_PASSWORD, value: "auth_pass" }

            - { name: SPRING_DATA_MONGODB_URI, value: "mongodb://mongo:27017/autenticacao-db" }
            - { name: JWT_SECRET, valueFrom: { secretKeyRef: { name: artesano-secrets, key: AUTH_JWT_SECRET_BASE64 } } }
            - { name: JWT_EXPIRATION, value: "28800" }
          readinessProbe:
            httpGet: { path: /actuator/health, port: 8081 }
            initialDelaySeconds: 40
            periodSeconds: 10
---
apiVersion: v1
kind: Service
metadata:
  name: autenticacao-service
  namespace: artesano
spec:
  selector: { app: autenticacao-service }
  ports:
    - name: http
      port: 8081
      targetPort: 8081
---
apiVersion: apps/v1
kind: Deployment
metadata:
  name: catalogo-service
  namespace: artesano
spec:
  replicas: 1
  selector:
    matchLabels: { app: catalogo-service }
  template:
    metadata:
      labels: { app: catalogo-service }
    spec:
      containers:
        - name: catalogo
          image: artesano/catalogo:0.1.0
          imagePullPolicy: IfNotPresent
          ports: [{ containerPort: 8082 }]
          envFrom:
            - configMapRef: { name: artesano-config }
            - secretRef: { name: artesano-secrets }
          env:
            - { name: SERVER_PORT, value: "8082" }
            - { name: SPRING_PROFILES_ACTIVE, value: "docker" }
            - { name: SPRING_APPLICATION_NAME, value: "CATALOGO-SERVICE" }

            - { name: SPRING_DATASOURCE_URL, value: "jdbc:postgresql://postgres-catalogo:5432/catalogo_db" }
            - { name: SPRING_DATASOURCE_USERNAME, value: "catalogo_user" }
            - { name: SPRING_DATASOURCE_PASSWORD, value: "catalogo_pass" }
            - { name: SPRING_FLYWAY_URL, value: "jdbc:postgresql://postgres-catalogo:5432/catalogo_db" }
            - { name: SPRING_FLYWAY_USER, value: "catalogo_user" }
            - { name: SPRING_FLYWAY_PASSWORD, value: "catalogo_pass" }

            - { name: AUTH_JWT_SECRET_BASE64, valueFrom: { secretKeyRef: { name: artesano-secrets, key: AUTH_JWT_SECRET_BASE64 } } }
          readinessProbe:
            httpGet: { path: /actuator/health, port: 8082 }
            initialDelaySeconds: 40
            periodSeconds: 10
---
apiVersion: v1
kind: Service
metadata:
  name: catalogo-service
  namespace: artesano
spec:
  selector: { app: catalogo-service }
  ports:
    - name: http
      port: 8082
      targetPort: 8082
---
apiVersion: apps/v1
kind: Deployment
metadata:
  name: estoque-service
  namespace: artesano
spec:
  replicas: 1
  selector:
    matchLabels: { app: estoque-service }
  template:
    metadata:
      labels: { app: estoque-service }
    spec:
      containers:
        - name: estoque
          image: artesano/estoque:0.1.0
          imagePullPolicy: IfNotPresent
          ports: [{ containerPort: 8083 }]
          envFrom:
            - configMapRef: { name: artesano-config }
            - secretRef: { name: artesano-secrets }
          env:
            - { name: SERVER_PORT, value: "8083" }
            - { name: SPRING_PROFILES_ACTIVE, value: "docker" }
            - { name: SPRING_APPLICATION_NAME, value: "ESTOQUE-SERVICE" }

            - { name: SPRING_DATASOURCE_URL, value: "jdbc:postgresql://postgres-estoque:5432/estoque_db" }
            - { name: SPRING_DATASOURCE_USERNAME, value: "estoque_user" }
            - { name: SPRING_DATASOURCE_PASSWORD, value: "estoque_pass" }
            - { name: SPRING_FLYWAY_URL, value: "jdbc:postgresql://postgres-estoque:5432/estoque_db" }
            - { name: SPRING_FLYWAY_USER, value: "estoque_user" }
            - { name: SPRING_FLYWAY_PASSWORD, value: "estoque_pass" }

            - { name: AUTH_JWT_SECRET_BASE64, valueFrom: { secretKeyRef: { name: artesano-secrets, key: AUTH_JWT_SECRET_BASE64 } } }
          readinessProbe:
            httpGet: { path: /actuator/health, port: 8083 }
            initialDelaySeconds: 40
            periodSeconds: 10
---
apiVersion: v1
kind: Service
metadata:
  name: estoque-service
  namespace: artesano
spec:
  selector: { app: estoque-service }
  ports:
    - name: http
      port: 8083
      targetPort: 8083
---
apiVersion: apps/v1
kind: Deployment
metadata:
  name: proposta-service
  namespace: artesano
spec:
  replicas: 1
  selector:
    matchLabels: { app: proposta-service }
  template:
    metadata:
      labels: { app: proposta-service }
    spec:
      containers:
        - name: proposta
          image: artesano/proposta:0.1.0
          imagePullPolicy: IfNotPresent
          ports: [{ containerPort: 8084 }]
          envFrom:
            - configMapRef: { name: artesano-config }
            - secretRef: { name: artesano-secrets }
          env:
            - { name: SERVER_PORT, value: "8084" }
            - { name: SPRING_PROFILES_ACTIVE, value: "docker" }
            - { name: SPRING_APPLICATION_NAME, value: "PROPOSTA-SERVICE" }

            - { name: SPRING_DATASOURCE_URL, value: "jdbc:postgresql://postgres-proposta:5432/proposta_db" }
            - { name: SPRING_DATASOURCE_USERNAME, value: "proposta_user" }
            - { name: SPRING_DATASOURCE_PASSWORD, value: "proposta_pass" }
            - { name: SPRING_FLYWAY_URL, value: "jdbc:postgresql://postgres-proposta:5432/proposta_db" }
            - { name: SPRING_FLYWAY_USER, value: "proposta_user" }
            - { name: SPRING_FLYWAY_PASSWORD, value: "proposta_pass" }

            - { name: AUTH_JWT_SECRET_BASE64, valueFrom: { secretKeyRef: { name: artesano-secrets, key: AUTH_JWT_SECRET_BASE64 } } }
          readinessProbe:
            httpGet: { path: /actuator/health, port: 8084 }
            initialDelaySeconds: 40
            periodSeconds: 10
---
apiVersion: v1
kind: Service
metadata:
  name: proposta-service
  namespace: artesano
spec:
  selector: { app: proposta-service }
  ports:
    - name: http
      port: 8084
      targetPort: 8084
---
apiVersion: apps/v1
kind: Deployment
metadata:
  name: api-gateway
  namespace: artesano
spec:
  replicas: 1
  selector:
    matchLabels: { app: api-gateway }
  template:
    metadata:
      labels: { app: api-gateway }
    spec:
      containers:
        - name: api-gateway
          image: artesano/api-gateway:0.1.0
          imagePullPolicy: IfNotPresent
          ports: [{ containerPort: 8080 }]
          envFrom:
            - configMapRef: { name: artesano-config }
          env:
            - { name: SERVER_PORT, value: "8080" }
            - { name: SPRING_PROFILES_ACTIVE, value: "docker" }
          readinessProbe:
            httpGet: { path: /actuator/health, port: 8080 }
            initialDelaySeconds: 30
            periodSeconds: 10
---
apiVersion: v1
kind: Service
metadata:
  name: api-gateway
  namespace: artesano
spec:
  selector: { app: api-gateway }
  ports:
    - name: http
      port: 8080
      targetPort: 8080
```

Aplique:

```powershell
kubectl apply -f k8s/10-apps.yaml
```

Confira:

```powershell
kubectl -n artesano get pods
kubectl -n artesano get svc
```

---

## 7) Acessar via port-forward (modo mais simples)

### 7.1 Gateway
```powershell
kubectl -n artesano port-forward svc/api-gateway 8080:8080
```

Teste no browser:
- `http://localhost:8080/actuator/health`

### 7.2 Eureka (registry)
Em outro terminal:

```powershell
kubectl -n artesano port-forward svc/registry 8761:8761
```

Abra:
- `http://localhost:8761`

---

## 8) Troubleshooting (quando algo cai)

Ver pods:
```powershell
kubectl -n artesano get pods
```

Ver logs do deployment:
```powershell
kubectl -n artesano logs deploy/autenticacao-service --tail=200
kubectl -n artesano logs deploy/catalogo-service --tail=200
kubectl -n artesano logs deploy/estoque-service --tail=200
kubectl -n artesano logs deploy/proposta-service --tail=200
kubectl -n artesano logs deploy/api-gateway --tail=200
kubectl -n artesano logs deploy/registry --tail=200
```

Ver detalhes do pod (erros de env/porta/volume):
```powershell
kubectl -n artesano describe pod <NOME_DO_POD>
```

---

## 9) Limpar tudo (reset)

Apagar apps:
```powershell
kubectl delete -f k8s/10-apps.yaml
kubectl delete -f k8s/00-config.yaml
kubectl delete -f k8s/zipkin.yaml
```

Remover Helm charts:
```powershell
helm uninstall postgres-auth -n artesano
helm uninstall postgres-catalogo -n artesano
helm uninstall postgres-estoque -n artesano
helm uninstall postgres-proposta -n artesano
helm uninstall mongo -n artesano
helm uninstall rabbitmq -n artesano
```

Apagar cluster:
```powershell
kind delete cluster --name artesano
```

---

## FAQ — "kind não é reconhecido"
1) Verifique se existe:
```powershell
where.exe kind
```

2) Confira se o winget instalou:
```powershell
winget list --id Kubernetes.kind
```

3) Reabra o PowerShell (PATH atualiza por sessão)

4) Instalação manual (fallback):
```powershell
mkdir C:\Tools\kind -Force
curl.exe -Lo C:\Tools\kind\kind.exe https://kind.sigs.k8s.io/dl/v0.27.0/kind-windows-amd64
setx PATH "$env:PATH;C:\Tools\kind"
```
Reabra o terminal e rode:
```powershell
kind version
```

---
