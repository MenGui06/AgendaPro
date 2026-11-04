import type { Appointment } from "./types";

/**
 * Camada de persistência. Hoje usa localStorage; para evoluir para banco /
 * multi-tenant, basta trocar a implementação mantendo esta interface.
 */
export interface AppointmentRepository {
  load(): Appointment[];
  save(items: Appointment[]): void;
}

const KEY = "agendapro:appointments:v1";

export const localRepository: AppointmentRepository = {
  load() {
    try {
      const raw = window.localStorage.getItem(KEY);
      return raw ? (JSON.parse(raw) as Appointment[]) : [];
    } catch {
      return [];
    }
  },
  save(items) {
    window.localStorage.setItem(KEY, JSON.stringify(items));
  },
};
