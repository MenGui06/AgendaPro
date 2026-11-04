package com.agendapro.appointment;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    List<Appointment> findByStartTimeBetweenOrderByStartTime(LocalDateTime from, LocalDateTime to);
}
