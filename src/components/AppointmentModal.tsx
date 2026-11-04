"use client";

import { useState } from "react";
import { toInputValue } from "@/lib/dates";
import type { Appointment, AppointmentInput } from "@/lib/types";

interface Props {
  appointment?: Appointment;
  defaultStart: Date;
  onSave: (data: AppointmentInput) => void;
  onDelete?: () => void;
  onClose: () => void;
}

export default function AppointmentModal({
  appointment,
  defaultStart,
  onSave,
  onDelete,
  onClose,
}: Props) {
  const initialStart = appointment ? new Date(appointment.start) : defaultStart;
  const initialEnd = appointment
    ? new Date(appointment.end)
    : new Date(defaultStart.getTime() + 60 * 60 * 1000);

  const [title, setTitle] = useState(appointment?.title ?? "");
  const [start, setStart] = useState(toInputValue(initialStart));
  const [end, setEnd] = useState(toInputValue(initialEnd));
  const [description, setDescription] = useState(appointment?.description ?? "");
  const [error, setError] = useState("");

  function submit(e: React.FormEvent) {
    e.preventDefault();
    const s = new Date(start);
    const en = new Date(end);
    if (!title.trim()) return setError("Informe um título.");
    if (isNaN(s.getTime()) || isNaN(en.getTime()))
      return setError("Datas inválidas.");
    if (en <= s) return setError("O fim deve ser depois do início.");
    onSave({
      title: title.trim(),
      start: s.toISOString(),
      end: en.toISOString(),
      description: description.trim(),
    });
  }

  return (
    <div className="overlay" onMouseDown={onClose}>
      <form
        className="modal"
        onSubmit={submit}
        onMouseDown={(e) => e.stopPropagation()}
      >
        <h2>{appointment ? "Editar agendamento" : "Novo agendamento"}</h2>
        <label>
          Título
          <input
            autoFocus
            value={title}
            onChange={(e) => setTitle(e.target.value)}
          />
        </label>
        <div className="row">
          <label>
            Início
            <input
              type="datetime-local"
              value={start}
              onChange={(e) => setStart(e.target.value)}
            />
          </label>
          <label>
            Fim
            <input
              type="datetime-local"
              value={end}
              onChange={(e) => setEnd(e.target.value)}
            />
          </label>
        </div>
        <label>
          Descrição
          <textarea
            rows={3}
            value={description}
            onChange={(e) => setDescription(e.target.value)}
          />
        </label>
        {error && <p className="error">{error}</p>}
        <div className="actions">
          {onDelete && (
            <button
              type="button"
              className="danger"
              onClick={() => {
                if (confirm("Excluir este agendamento?")) onDelete();
              }}
            >
              Excluir
            </button>
          )}
          <span className="spacer" />
          <button type="button" onClick={onClose}>
            Cancelar
          </button>
          <button type="submit" className="primary">
            Salvar
          </button>
        </div>
      </form>
    </div>
  );
}
