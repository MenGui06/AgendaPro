package com.agendapro.calendar;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.stereotype.Service;

import com.agendapro.appointment.AppointmentRepository;

@Service
public class CalendarService {

    public static final String MONTH = "month";
    public static final String WEEK = "week";

    private static final Locale PT_BR = Locale.forLanguageTag("pt-BR");
    private static final List<String> WEEKDAYS = List.of("Dom", "Seg", "Ter", "Qua", "Qui", "Sex", "Sáb");

    private final AppointmentRepository repository;

    public CalendarService(AppointmentRepository repository) {
        this.repository = repository;
    }

    public static String normalizeView(String view) {
        return WEEK.equals(view) ? WEEK : MONTH;
    }

    public CalendarPage build(String view, LocalDate date) {
        view = normalizeView(view);
        LocalDate today = LocalDate.now();
        LocalDate ref = date != null ? date : today;
        boolean month = view.equals(MONTH);

        LocalDate first = month ? ref.withDayOfMonth(1) : ref;
        LocalDate start = first.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));
        int count = month ? 42 : 7;
        LocalDate end = start.plusDays(count - 1);

        var byDay = repository
                .findByStartTimeBetweenOrderByStartTime(start.atStartOfDay(), end.atTime(LocalTime.MAX))
                .stream()
                .collect(Collectors.groupingBy(a -> a.getStartTime().toLocalDate()));

        List<CalendarDay> days = IntStream.range(0, count)
                .mapToObj(start::plusDays)
                .map(d -> new CalendarDay(d,
                        WEEKDAYS.get(d.getDayOfWeek().getValue() % 7) + " " + d.getDayOfMonth(),
                        d.equals(today),
                        month && d.getMonth() != ref.getMonth(),
                        byDay.getOrDefault(d, List.of())))
                .toList();

        String title = month
                ? ref.format(DateTimeFormatter.ofPattern("MMMM 'de' yyyy", PT_BR))
                : start.format(DateTimeFormatter.ofPattern("dd MMM", PT_BR)) + " – "
                        + end.format(DateTimeFormatter.ofPattern("dd MMM yyyy", PT_BR));

        return new CalendarPage(view, title, WEEKDAYS, days, ref, today,
                month ? first.minusMonths(1) : ref.minusWeeks(1),
                month ? first.plusMonths(1) : ref.plusWeeks(1));
    }
}
