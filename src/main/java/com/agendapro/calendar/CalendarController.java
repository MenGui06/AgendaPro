package com.agendapro.calendar;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class CalendarController {

    private final CalendarService service;

    public CalendarController(CalendarService service) {
        this.service = service;
    }

    @GetMapping("/")
    public String calendar(@RequestParam(defaultValue = "month") String view,
                           @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                           Model model) {
        CalendarPage page = service.build(view, date);
        model.addAttribute("view", page.view());
        model.addAttribute("title", page.title());
        model.addAttribute("weekdays", page.weekdays());
        model.addAttribute("days", page.days());
        model.addAttribute("ref", page.ref());
        model.addAttribute("today", page.today());
        model.addAttribute("prev", page.prev());
        model.addAttribute("next", page.next());
        return "calendar/calendar";
    }
}
