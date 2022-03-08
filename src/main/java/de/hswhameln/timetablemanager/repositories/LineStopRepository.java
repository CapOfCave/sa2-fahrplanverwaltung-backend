package de.hswhameln.timetablemanager.repositories;

import de.hswhameln.timetablemanager.entities.LineStop;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface LineStopRepository extends JpaRepository<LineStop, Long> {

    List<LineStop> findByLineIdOrderByIndex(long lineId);

    int countByLineId(long lineId);
}
