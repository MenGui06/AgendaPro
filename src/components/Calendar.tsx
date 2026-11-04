"use client";

import { useEffect, useMemo, useState } from "react";
import AppointmentModal from "./AppointmentModal";
import {
  addDays,
  formatTime,
  isSameDay,
  monthGrid,
  startOfDay,
  weekDays,
} from "@/lib/dates";
import { localRepository } from "@/lib/storage";
import type { Appointment, AppointmentInput } from "@/lib/types";

type View = "month" | "week";

const WEEKDAYS = ["Dom", "Seg", "Ter", "Qua", "Qui", "Sex", "Sáb"];
const HOURS = Array.from({ length: 24 }, (_, i) => i);
const HOUR_PX = 48;

type ModalState =
  | { mode: "create"; start: Date }
  | { mode: "edit"; appointment: Appointment }
  | null;

export default function Calendar() {
  const [view, setView] = useState<View>("month");
  const [ref, setRef] = useState(() => startOfDay(new Date()));
  const [items, setItems] = useState<Appointment[]>([]);
  const [loaded, setLoaded] = useState(false);
  const [modal, setModal] = useState<ModalState>(null);

  useEffect(() => {
    setItems(localRepository.load());
    setLoaded(true);
  }, []);

  useEffect(() => {
    if (loaded) localRepository.save(items);
  }, [items, loaded]);

  const days = useMemo(
    () => (view === "month" ? monthGrid(ref) : weekDays(ref)),
    [view, ref]
  );

  const forDay = (d: Date) =>
    items
      .filter((a) => isSameDay(new Date(a.start), d))
      .sort((a, b) => a.start.localeCompare(b.start));

  function navigate(dir: -1 | 1) {
    if (view === "month")
      setRef(new Date(ref.getFullYear(), ref.getMonth() + dir, 1));
    else setRef(addDays(ref, 7 * dir));
  }

  function save(data: AppointmentInput) {
    if (modal?.mode === "edit") {
      const id = modal.appointment.id;
      setItems((prev) => prev.map((a) => (a.id === id ? { ...data, id } : a)));
    } else {
      setItems((prev) => [...prev, { ...data, id: crypto.randomUUID() }]);
    }
    setModal(null);
  }

  function remove() {
    if (modal?.mode !== "edit") return;
    const id = modal.appointment.id;
    setItems((prev) => prev.filter((a) => a.id !== id));
    setModal(null);
  }

  const title =
    view === "month"
      ? ref.toLocaleDateString("pt-BR", { month: "long", year: "numeric" })
      : `${days[0].toLocaleDateString("pt-BR", {
          day: "2-digit",
          month: "short",
        })} – ${days[6].toLocaleDateString("pt-BR", {
          day: "2-digit",
          month: "short",
          year: "numeric",
        })}`;

  const today = new Date();

  const chip = (a: Appointment) => (
    <button
      key={a.id}
      className="chip"
      title={a.description || a.title}
      onClick={(e) => {
        e.stopPropagation();
        setModal({ mode: "edit", appointment: a });
      }}
    >
      {formatTime(new Date(a.start))} {a.title}
    </button>
  );

  return (
    <main className="app">
      <header className="toolbar">
        <h1>AgendaPro</h1>
        <div className="nav">
          <button onClick={() => navigate(-1)} aria-label="Anterior">
            ‹
          </button>
          <button onClick={() => setRef(startOfDay(new Date()))}>Hoje</button>
          <button onClick={() => navigate(1)} aria-label="Próximo">
            ›
          </button>
        </div>
        <h2 className="period">{title}</h2>
        <div className="views">
          <button
            className={view === "month" ? "active" : ""}
            onClick={() => setView("month")}
          >
            Mês
          </button>
          <button
            className={view === "week" ? "active" : ""}
            onClick={() => setView("week")}
          >
            Semana
          </button>
        </div>
        <button
          className="primary"
          onClick={() => {
            const s = new Date();
            s.setMinutes(0, 0, 0);
            s.setHours(s.getHours() + 1);
            setModal({ mode: "create", start: s });
          }}
        >
          + Novo
        </button>
      </header>

      {view === "month" ? (
        <section className="month">
          {WEEKDAYS.map((w) => (
            <div key={w} className="dow">
              {w}
            </div>
          ))}
          {days.map((d) => {
            const list = forDay(d);
            return (
              <div
                key={d.toISOString()}
                className={
                  "cell" +
                  (d.getMonth() !== ref.getMonth() ? " muted" : "") +
                  (isSameDay(d, today) ? " today" : "")
                }
                onClick={() => {
                  const s = new Date(d);
                  s.setHours(9, 0, 0, 0);
                  setModal({ mode: "create", start: s });
                }}
              >
                <span className="num">{d.getDate()}</span>
                {list.slice(0, 3).map(chip)}
                {list.length > 3 && (
                  <span className="more">+{list.length - 3} mais</span>
                )}
              </div>
            );
          })}
        </section>
      ) : (
        <section className="week">
          <div className="week-head">
            <div />
            {days.map((d) => (
              <div
                key={d.toISOString()}
                className={"dow" + (isSameDay(d, today) ? " today" : "")}
              >
                {WEEKDAYS[d.getDay()]} {d.getDate()}
              </div>
            ))}
          </div>
          <div className="week-body">
            <div className="hours">
              {HOURS.map((h) => (
                <div key={h} style={{ height: HOUR_PX }}>
                  {String(h).padStart(2, "0")}:00
                </div>
              ))}
            </div>
            {days.map((d) => (
              <div
                key={d.toISOString()}
                className="daycol"
                style={{ height: HOUR_PX * 24 }}
                onClick={(e) => {
                  const rect = e.currentTarget.getBoundingClientRect();
                  const hour = Math.min(
                    23,
                    Math.floor((e.clientY - rect.top) / HOUR_PX)
                  );
                  const s = new Date(d);
                  s.setHours(hour, 0, 0, 0);
                  setModal({ mode: "create", start: s });
                }}
              >
                {HOURS.map((h) => (
                  <div
                    key={h}
                    className="slot"
                    style={{ top: h * HOUR_PX, height: HOUR_PX }}
                  />
                ))}
                {forDay(d).map((a) => {
                  const s = new Date(a.start);
                  const e = new Date(a.end);
                  const top = (s.getHours() + s.getMinutes() / 60) * HOUR_PX;
                  const endSameDay = isSameDay(s, e);
                  const endH = endSameDay
                    ? e.getHours() + e.getMinutes() / 60
                    : 24;
                  const height = Math.max(20, (endH - top / HOUR_PX) * HOUR_PX);
                  return (
                    <button
                      key={a.id}
                      className="event"
                      style={{ top, height }}
                      title={a.description || a.title}
                      onClick={(ev) => {
                        ev.stopPropagation();
                        setModal({ mode: "edit", appointment: a });
                      }}
                    >
                      <strong>{a.title}</strong>
                      <br />
                      {formatTime(s)}–{formatTime(e)}
                    </button>
                  );
                })}
              </div>
            ))}
          </div>
        </section>
      )}

      {modal && (
        <AppointmentModal
          appointment={modal.mode === "edit" ? modal.appointment : undefined}
          defaultStart={modal.mode === "create" ? modal.start : new Date()}
          onSave={save}
          onDelete={modal.mode === "edit" ? remove : undefined}
          onClose={() => setModal(null)}
        />
      )}
    </main>
  );
}
