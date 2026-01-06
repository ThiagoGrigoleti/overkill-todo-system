#  Enterprise-Grade Distributed To-Do List™

> **"Fazer um INSERT? Não, orquestrar containers!"**

---

##  Sobre o Projeto

Este projeto é uma **Lista de Tarefas (To-Do List)** projetada para resolver um problema trivial da forma mais complexa, cara e academicamente impressionante possível.

O sistema utiliza **Event-Driven Architecture** e **CQRS (Command Query Responsibility Segregation)** para garantir que sua lista de compras tenha alta disponibilidade, tolerância a falhas e escalabilidade web-scale mesmo que o único usuário seja eu mesmo.

---

##  Arquitetura

O sistema abandona o ultrapassado conceito de **request-response síncrono** em favor de um fluxo **assíncrono moderno**, garantindo **consistência eventual**.

###  O Fluxo "Rube Goldberg"

1. O **Client** envia uma tarefa para o **todo-command-service**.

2. O **Command Service** recebe a requisição, não faz absolutamente nada útil com ela além de:
   - Converter para JSON
   - Publicar a mensagem no **RabbitMQ** (Direct Exchange)

3. O cliente recebe um **202 Accepted**, porque quem tem pressa claramente não entende microsserviços.

4. O **RabbitMQ** mantém a mensagem na fila `task.created.queue` com persistência em disco (porque paranoia é um requisito não funcional).

5. O **Query Worker Service** acorda, consome a mensagem, finge processamento pesado (`Thread.sleep`) e finalmente:
   - Persiste os dados no **PostgreSQL** (ACID é vida).
   - Invalida e atualiza o cache no **Redis**.

6. **Observabilidade total**: todo o trajeto é monitorado (em teoria).

---

##  Tech Stack (A Bazuca)

| Tecnologia | Função | Justificativa Enterprise |
|------------|--------|--------------------------|
| **Java 21** | Linguagem | Porque eu gosto de tipos fortes e `public static void main`. |
| **Spring Boot 3** | Framework | O padrão de mercado para microsserviços robustos |
| **RabbitMQ** | Message Broker | Desacoplamento total. |
| **PostgreSQL** | Database | Persistência relacional confiável para armazenar Strings de 20 caracteres. |
| **Redis** | Cache | Garantir leitura em sub-milissegundos de uma lista com 3 itens. |
| **Docker** | Containerização | "Na minha máquina funciona" agora funciona na sua também. |
| **Kubernetes** | Orquestração | Preparado para escalar horizontalmente caso eu decida comprar uma rede inteira de super mercado. |

---

## 🚀 Como Rodar (Localmente)

Você não precisa de um cluster AWS, apenas **Docker** instalado e um pouco de resignação.

###  Pré-requisitos

- **Docker & Docker Compose**
- **Java 21 (JDK)**
- **Maven**

###  Passo a Passo

1. **Clone o repositório:**

```bash
git clone https://github.com/seu-usuario/overkill-todo-list.git
cd overkill-todo-list
```

2. **Suba a infraestrutura (Banco, Cache, Fila):**

```bash
docker-compose up -d
```

3. **Compile e rode os serviços:**

**Terminal 1 — Command Service**

```bash
cd todo-command-service
./mvnw spring-boot:run
```

**Terminal 2 — Worker Service**

```bash
cd todo-query-worker
./mvnw spring-boot:run
```

---

## 📡 API Endpoints

### **Criar Tarefa (Async)**

**POST** `/api/v1/tasks`

```json
{
  "title": "Aprender Kubernetes",
  "description": "Porque o que adianta saber Hello World se não temos um cluster?"
}
```

**Response:** `202 Accepted`

> "Sua tarefa foi recebida e está navegando pelo limbo da nossa infraestrutura distribuída."

---

##  Roadmap (Futuro)

- [ ] Substituir **RabbitMQ** por **Kafka** (porque RabbitMQ é muito 2018).
- [ ] Adicionar **Elasticsearch** para indexar as 3 tarefas e permitir busca full-text.
- [ ] Criar um frontend em **Next.js** com SSR hospedado em Edge Computing, porque aqui nos preocupamos com os milissegundos de tempo de reposta da computação em nuvem.
- [ ] Migrar para **Serverless** e depois voltar para **Monólito**, apenas pela experiência espiritual.

---

## 🤝 Contribuição

**Pull requests** são bem-vindos, desde que:

- Aumentem a complexidade ciclomática do código
- Introduzam pelo menos uma nova ferramenta de infraestrutura desnecessária.
- Não exclua microsserviços existentes
- Sigam fielmente a filosofia de desenvolvimento do extreme-go-horse (caso não saiba o que é, vale a pena conferir)

---

## 📄 Licença

Distribuído sob a licença **MIT**.  (Porque MIT é bem legal né, porque não?)
Não me responsabilizo caso alguém use duas RTX 5090 em nuvem pra armazenar a lista de mercado na AWS.
