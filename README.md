# HubSpot API Integration 🚀

Este projeto é uma API Java Spring Boot que integra com a plataforma HubSpot para:

- ✅ Receber eventos via Webhook (ex: criação de contatos)
- ✅ Persistir contatos no banco PostgreSQL
- ✅ Realizar autenticação OAuth2 com HubSpot
- ✅ Exportar logs de eventos para CSV
- ✅ Exibir dados via endpoints REST com paginação

---

## 🛠️ Tecnologias Utilizadas

- Java 21 + Spring Boot 3
- PostgreSQL
- Flyway (migrations)
- Docker + Docker Compose
- JUnit + Mockito
- Lombok

---

## 🚀 Subindo o Projeto com Docker

### 1. Gere o `.jar`
```bash
mvn clean package -DskipTests
```

### 2. Execute o Docker Compose
```bash
docker-compose up --build
```

- A aplicação ficará disponível em: `http://localhost:8080`
- O banco estará exposto em: `localhost:5433` com user `postgres` e senha `root`

---

## 🔐 Autenticação OAuth2 HubSpot

1. Acesse o endpoint:
```http
GET http://localhost:8080/oauth/authorize-url
```

2. Siga a URL gerada e autorize.
3. Após o redirecionamento, copie o `code` da URL e acesse:
```http
GET http://localhost:8080/oauth/callback?code=SEU_CODE_AQUI
```

---

## 📬 Webhooks

- Configure os Webhooks no painel do HubSpot apontando para:
```http
POST http://localhost:8080/webhook/hubspot
```

---

## 📚 Endpoints Principais

### 📇 Contatos
- `POST /hubspot/contact` – Criar contato no HubSpot
- `GET /hubspot/contact` – Listar contatos com paginação
- `GET /hubspot/contact/{id}` – Buscar contato por ID

### 📜 Logs de Webhook
- `GET /webhook/logs` – Listar logs de eventos
- `GET /webhook/logs/export` – Exportar logs em CSV

---

## ✅ Testes
- Testes unitários para services e controllers usando JUnit + Mockito.

Para rodar:
```bash
mvn test
```

---

## 📁 Estrutura do Projeto
```
├── src/main/java
│   └── controller
│   └── service
│   └── model
│   └── repository
├── src/main/resources
│   └── application.yml
│   └── db/migration
├── Dockerfile
├── docker-compose.yml
├── pom.xml
└── README.md
```

---

## 🙋‍♂️ Autor

Feito com 💛 por Pedro — desafio técnico HubSpot Java Backend.

---

## 📄 Documentação Técnica

### 🔍 Decisões Arquiteturais

- **Spring Boot**: escolhido pela facilidade de configurar REST APIs, injeção de dependências e ecossistema sólido para integração com JPA, testes e OAuth.
- **PostgreSQL**: banco relacional robusto, open source e com bom suporte à tipagem e integração com Spring Data.
- **Flyway**: utilizado para controle de versionamento e execução automática de scripts SQL, evitando divergência de schema.
- **Webhook como gatilho principal**: qualquer criação de contato no HubSpot é refletida localmente via evento push (evita pooling e melhora performance).
- **Token renovável em memória**: o token OAuth2 é armazenado em memória e atualizado automaticamente se expirar, simplificando o fluxo.

### 📚 Bibliotecas Utilizadas

| Biblioteca         | Uso                                                   |
|--------------------|--------------------------------------------------------|
| Spring Web         | Criação dos endpoints REST                            |
| Spring Data JPA    | Acesso e persistência no banco PostgreSQL             |
| Lombok             | Redução de boilerplate (getters/setters/builders)     |
| Jackson            | Serialização e desserialização JSON                   |
| RestTemplate       | Comunicação com APIs HubSpot                          |
| Flyway             | Versionamento de banco com SQL controlado             |
| JUnit + Mockito    | Testes unitários com injeção e simulação de dependências |
| Docker Compose     | Subida de containers app e banco                      |

### 💡 Melhorias Futuras

- 🔐 **Persistência segura dos tokens OAuth2** (ex: Redis, banco ou secrets manager).
- 💾 **Histórico de atualizações de contatos**, com auditoria de campos alterados.
- 📊 **Dashboard visual com gráficos**, mostrando número de webhooks por dia, novos contatos por origem, etc.
- 📡 **WebSocket ou SSE para atualizar contatos em tempo real**.
- ✅ **Testes de integração com Testcontainers** (para PostgreSQL e chamadas simuladas do HubSpot).
- 🌍 **Deploy automatizado com CI/CD (GitHub Actions)** + ambiente staging.