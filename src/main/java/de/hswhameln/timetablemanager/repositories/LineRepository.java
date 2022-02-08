package de.hswhameln.timetablemanager.repositories;

import de.hswhameln.timetablemanager.entities.Line;
import org.springframework.data.repository.CrudRepository;

public interface LineRepository extends CrudRepository<Line, Long> {
}
