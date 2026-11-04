# AgendaPro

Sistema de agendas: calendário **mês** e **semana** para criar, editar e excluir agendamentos (título, início, fim e descrição).

## Stack

- Java 25 + Spring Boot (Spring MVC, Spring Data JPA)
- Thymeleaf (páginas HTML renderizadas no servidor) + CSS puro
- Banco H2 em arquivo (`./data`), sem instalação extra

## Como executar

Requer **JDK 25 ou superior**. O Maven já vem embutido (`mvnw`).

```powershell
.\mvnw spring-boot:run
```

Acesse http://localhost:8080. Para parar, `Ctrl+C`.

Testes: `.\mvnw test` — Build: `.\mvnw package`

## Estrutura

```
src/main/java/com/agendapro/
  AgendaProApplication.java      ponto de entrada
  Appointment.java               entidade (campos + validações)
  AppointmentRepository.java     acesso ao banco
  CalendarController.java        rotas: calendário e CRUD de agendamentos
src/main/resources/
  templates/calendar.html        páginas Thymeleaf (visões mês/semana)
  templates/form.html            formulário de criar/editar
  static/style.css               todo o CSS
  application.properties         configuração (banco etc.)
```

## Como adicionar funcionalidades

- **Novo campo** (ex.: local, cor): adicione em `Appointment.java` e no `form.html`; o banco é atualizado automaticamente (`ddl-auto=update`).
- **Nova página/rota**: método novo em `CalendarController.java` + um `.html` em `templates/`.
- **Nova consulta** (ex.: busca por título): método novo em `AppointmentRepository.java`.
- **Outro banco** (Postgres/MySQL): troque o driver no `pom.xml` e as linhas `spring.datasource.*` em `application.properties`.
