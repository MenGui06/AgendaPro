package com.agendapro;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.validation.Valid;

@Controller
public class CalendarController {

    private static final Locale PT_BR = Locale.forLanguageTag("pt-BR");
    private static final List<String> WEEKDAYS = List.of("Dom", "Seg", "Ter", "Qua", "Qui", "Sex", "Sáb");

    /** Um dia exibido no calendário, com seus agendamentos. */
    public record Day(LocalDate date, String label, boolean today, boolean outsideMonth,
                      List<Appointment> items) {
    }

    private final AppointmentRepository repository;

    public CalendarController(AppointmentRepository repository) {
        this.repository = repository;
    }

    // ---------- Calendário ----------

    @GetMapping("/")
    public String calendar(@RequestParam(defaultValue = "month") String view,
                           @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                           Model model) {
        view = normalizeView(view);
        LocalDate today = LocalDate.now();
        LocalDate ref = date != null ? date : today;
        boolean month = view.equals("month");

        LocalDate first = month ? ref.withDayOfMonth(1) : ref;
        LocalDate start = first.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));
        int count = month ? 42 : 7;
        LocalDate end = start.plusDays(count - 1);

        var byDay = repository
                .findByStartTimeBetweenOrderByStartTime(start.atStartOfDay(), end.atTime(LocalTime.MAX))
                .stream()
                .collect(Collectors.groupingBy(a -> a.getStartTime().toLocalDate()));

        List<Day> days = IntStream.range(0, count)
                .mapToObj(start::plusDays)
                .map(d -> new Day(d,
                        WEEKDAYS.get(d.getDayOfWeek().getValue() % 7) + " " + d.getDayOfMonth(),
                        d.equals(today),
                        month && d.getMonth() != ref.getMonth(),
                        byDay.getOrDefault(d, List.of())))
                .toList();

        String title = month
                ? ref.format(DateTimeFormatter.ofPattern("MMMM 'de' yyyy", PT_BR))
                : start.format(DateTimeFormatter.ofPattern("dd MMM", PT_BR)) + " – "
                        + end.format(DateTimeFormatter.ofPattern("dd MMM yyyy", PT_BR));

        model.addAttribute("view", view);
        model.addAttribute("title", title);
        model.addAttribute("weekdays", WEEKDAYS);
        model.addAttribute("days", days);
        model.addAttribute("ref", ref);
        model.addAttribute("today", today);
        model.addAttribute("prev", month ? first.minusMonths(1) : ref.minusWeeks(1));
        model.addAttribute("next", month ? first.plusMonths(1) : ref.plusWeeks(1));
        return "calendar";
    }

    // ---------- Agendamentos ----------

    @GetMapping("/appointments/new")
    public String newForm(@RequestParam(defaultValue = "month") String view,
                          @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                          Model model) {
        LocalDateTime start = (date != null ? date : LocalDate.now()).atTime(9, 0);
        Appointment appointment = new Appointment();
        appointment.setStartTime(start);
        appointment.setEndTime(start.plusHours(1));
        return form(appointment, view, model);
    }

    @GetMapping("/appointments/{id}/edit")
    public String editForm(@PathVariable Long id,
                           @RequestParam(defaultValue = "month") String view,
                           Model model) {
        Appointment appointment = repository.findById(id).orElse(null);
        if (appointment == null) {
            return redirectToCalendar(view, LocalDate.now());
        }
        return form(appointment, view, model);
    }

    @PostMapping("/appointments")
    public String save(@Valid @ModelAttribute("appointment") Appointment appointment,
                       BindingResult result,
                       @RequestParam(defaultValue = "month") String view,
                       Model model) {
        if (appointment.getStartTime() != null && appointment.getEndTime() != null
                && !appointment.getEndTime().isAfter(appointment.getStartTime())) {
            result.rejectValue("endTime", "invalid", "O fim deve ser depois do início.");
        }
        if (result.hasErrors()) {
            return form(appointment, view, model);
        }
        appointment.setTitle(appointment.getTitle().trim());
        repository.save(appointment);
        return redirectToCalendar(view, appointment.getStartTime().toLocalDate());
    }

    @PostMapping("/appointments/{id}/delete")
    public String delete(@PathVariable Long id, @RequestParam(defaultValue = "month") String view) {
        LocalDate date = repository.findById(id)
                .map(a -> a.getStartTime().toLocalDate())
                .orElse(LocalDate.now());
        repository.deleteById(id);
        return redirectToCalendar(view, date);
    }

    // ---------- Auxiliares ----------

    private String form(Appointment appointment, String view, Model model) {
        LocalDate back = appointment.getStartTime() != null
                ? appointment.getStartTime().toLocalDate()
                : LocalDate.now();
        model.addAttribute("appointment", appointment);
        model.addAttribute("view", normalizeView(view));
        model.addAttribute("back", back);
        return "form";
    }

    private static String redirectToCalendar(String view, LocalDate date) {
        return "redirect:/?view=" + normalizeView(view) + "&date=" + date;
    }

    private static String normalizeView(String view) {
        return "week".equals(view) ? "week" : "month";
    }
}
