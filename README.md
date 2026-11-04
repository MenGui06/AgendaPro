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
  AgendaProApplication.java          ponto de entrada
  appointment/                       funcionalidade: agendamentos
    Appointment.java                   entidade (campos + validações)
    AppointmentRepository.java         acesso ao banco
    AppointmentController.java         rotas: criar, editar, excluir
  calendar/                          funcionalidade: calendário
    CalendarController.java            rota da tela principal
    CalendarService.java               monta a grade de mês/semana
    CalendarDay.java, CalendarPage.java  dados enviados à página
src/main/resources/
  templates/calendar/calendar.html   visões mês/semana
  templates/appointment/form.html    formulário de criar/editar
  templates/fragments/head.html      <head> compartilhado
  static/css/style.css               todo o CSS
  application.properties             configuração (banco etc.)
```

## Como adicionar funcionalidades

Cada funcionalidade nova ganha seu pacote em `com/agendapro/` e sua pasta em `templates/` (ex.: `client/`, `service/`).

- **Novo campo** (ex.: local, cor): adicione em `Appointment.java` e no `appointment/form.html`; o banco é atualizado automaticamente (`ddl-auto=update`).
- **Nova página/rota**: método novo no controller da funcionalidade + um `.html` na pasta dela em `templates/`.
- **Nova consulta** (ex.: busca por título): método novo em `AppointmentRepository.java`.
- **Outro banco** (Postgres/MySQL): troque o driver no `pom.xml` e as linhas `spring.datasource.*` em `application.properties`.
