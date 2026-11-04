package com.agendapro.calendar;

import java.time.LocalDate;
import java.util.List;

/** Dados prontos para renderizar a tela do calendário. */
public record CalendarPage(String view, String title, List<String> weekdays, List<CalendarDay> days,
                           LocalDate ref, LocalDate today, LocalDate prev, LocalDate next) {
}
