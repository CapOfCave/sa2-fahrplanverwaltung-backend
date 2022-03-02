package de.hswhameln.timetablemanager.repositories;

import de.hswhameln.timetablemanager.entities.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    List<Schedule> findByLineIdIn(List<Long> lineIds);
}
