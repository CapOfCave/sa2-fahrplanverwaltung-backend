package de.hswhameln.timetablemanager.services;

import de.hswhameln.timetablemanager.entities.Line;
import de.hswhameln.timetablemanager.entities.LineStop;
import de.hswhameln.timetablemanager.repositories.BusStopRepository;
import de.hswhameln.timetablemanager.repositories.LineRepository;
import de.hswhameln.timetablemanager.repositories.LineStopRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

@Service
public class LineService {

    private final LineRepository lineRepository;
    private final BusStopRepository busStopRepository;
    private final LineStopRepository lineStopRepository;

    @Autowired
    public LineService(LineRepository lineRepository, BusStopRepository busStopRepository, LineStopRepository lineStopRepository) {
        this.lineRepository = lineRepository;
        this.busStopRepository = busStopRepository;
        this.lineStopRepository = lineStopRepository;
    }

    public Line createLine(String name) {
        // TODO exception handling
        var line = new Line(name);
        return this.lineRepository.save(line);
    }

    public Collection<Line> getLines() {
        return new ArrayList<>(this.lineRepository.findAll());
    }

    public void deleteLine(long id) {
        this.lineRepository.deleteById(id);
    }

    public Line modifyLine(long id, String name) {
        Line line = this.lineRepository.findById(id).orElseThrow();
        line.setName(name);
        return this.lineRepository.save(line);
    }

    public Optional<Line> getLine(long id) {
        return this.lineRepository.findById(id);
    }

    public void addBusStop(long lineId, long busStopId, Integer secondsToNextStop) {
        var line = this.lineRepository.getById(lineId);
        var busStop = this.busStopRepository.getById(busStopId);
        long index = createNewIndex(lineId);
        LineStop lineStop = new LineStop(index, secondsToNextStop, line, busStop);
        this.lineStopRepository.save(lineStop);

    }

    public Collection<LineStop> getBusStops(long id) {
        return this.lineStopRepository.findByLineIdOrderByIndex(id);
    }

    /**
     * Create a new index for a new line stop.
     */
    private long createNewIndex(long lineId) {
        return this.lineStopRepository.countByLineId(lineId);
    }

}
