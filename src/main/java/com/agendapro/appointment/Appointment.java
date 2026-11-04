package com.agendapro.appointment;

import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
public class Appointment {

    private static final String DATETIME_LOCAL = "yyyy-MM-dd'T'HH:mm";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Informe um título.")
    private String title;

    @NotNull(message = "Informe o início.")
    @DateTimeFormat(pattern = DATETIME_LOCAL)
    private LocalDateTime startTime;

    @NotNull(message = "Informe o fim.")
    @DateTimeFormat(pattern = DATETIME_LOCAL)
    private LocalDateTime endTime;

    @Column(length = 2000)
    private String description;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }

    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
