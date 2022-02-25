package de.hswhameln.timetablemanager.repositories;

import de.hswhameln.timetablemanager.entities.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
}
