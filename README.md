# AgendaPro

MVP de um sistema de agendas (SaaS): calendário com visões **mês** e **semana** para criar, visualizar, editar e excluir agendamentos (título, início, fim e descrição).

## Stack

- Next.js (App Router) + React + TypeScript
- Persistência local via `localStorage` (navegador)

## Como executar

Requer Node.js 18.18+.

```bash
npm install
npm run dev      # http://localhost:3000
```

Build de produção:

```bash
npm run build
npm start
```

## Uso

- Clique em um dia (mês) ou em um horário (semana) para criar um agendamento; ou use **+ Novo**.
- Clique em um agendamento para editar ou excluir.
- Use ‹ / › / **Hoje** para navegar e **Mês / Semana** para alternar a visão.

## Estrutura

- `src/components/Calendar.tsx` – calendário e visões
- `src/components/AppointmentModal.tsx` – formulário de agendamento
- `src/lib/storage.ts` – interface `AppointmentRepository` (hoje `localStorage`)
- `src/lib/dates.ts` – utilitários de data

## Próximos passos

Para evoluir para banco/multi-tenant, implemente `AppointmentRepository` sobre uma API/banco (ex.: Postgres + Prisma) e adicione autenticação e `tenantId` ao modelo `Appointment`.