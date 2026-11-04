package com.agendapro.appointment;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.agendapro.calendar.CalendarService;

import jakarta.validation.Valid;

@Controller
public class AppointmentController {

    private final AppointmentRepository repository;

    public AppointmentController(AppointmentRepository repository) {
        this.repository = repository;
    }

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

    private String form(Appointment appointment, String view, Model model) {
        LocalDate back = appointment.getStartTime() != null
                ? appointment.getStartTime().toLocalDate()
                : LocalDate.now();
        model.addAttribute("appointment", appointment);
        model.addAttribute("view", CalendarService.normalizeView(view));
        model.addAttribute("back", back);
        return "appointment/form";
    }

    private static String redirectToCalendar(String view, LocalDate date) {
        return "redirect:/?view=" + CalendarService.normalizeView(view) + "&date=" + date;
    }
}
