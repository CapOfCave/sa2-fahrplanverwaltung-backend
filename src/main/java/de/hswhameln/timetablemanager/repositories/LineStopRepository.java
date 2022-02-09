package de.hswhameln.timetablemanager.repositories;

import de.hswhameln.timetablemanager.entities.LineStop;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;

public interface LineStopRepository extends JpaRepository<LineStop, Long> {

    Collection<LineStop> findByLineIdOrderByIndex(long lineId);

    int countByLineId(long lineId);
}
