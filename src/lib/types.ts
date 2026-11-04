export interface Appointment {
  id: string;
  title: string;
  /** ISO 8601 */
  start: string;
  /** ISO 8601 */
  end: string;
  description: string;
}

export type AppointmentInput = Omit<Appointment, "id">;
