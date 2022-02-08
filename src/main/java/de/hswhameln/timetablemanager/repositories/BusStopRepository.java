package de.hswhameln.timetablemanager.repositories;

import de.hswhameln.timetablemanager.entities.BusStop;
import org.springframework.data.repository.CrudRepository;

public interface BusStopRepository extends CrudRepository<BusStop, Long> {
}
