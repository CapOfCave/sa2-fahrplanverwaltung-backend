package de.hswhameln.timetablemanager.repositories;

import de.hswhameln.timetablemanager.entities.BusStop;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BusStopRepository extends JpaRepository<BusStop, Long> {
    boolean existsByName(String name);
}
