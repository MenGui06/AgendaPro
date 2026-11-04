package com.agendapro.calendar;

import java.time.LocalDate;
import java.util.List;

import com.agendapro.appointment.Appointment;

/** Um dia exibido no calendário, com seus agendamentos. */
public record CalendarDay(LocalDate date, String label, boolean today, boolean outsideMonth,
                          List<Appointment> items) {
}
