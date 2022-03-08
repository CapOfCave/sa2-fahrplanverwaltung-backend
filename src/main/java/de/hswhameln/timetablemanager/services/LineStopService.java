package de.hswhameln.timetablemanager.services;

import de.hswhameln.timetablemanager.entities.Line;
import de.hswhameln.timetablemanager.entities.LineStop;
import de.hswhameln.timetablemanager.exceptions.BusStopNotFoundException;
import de.hswhameln.timetablemanager.exceptions.LineNotFoundException;
import de.hswhameln.timetablemanager.exceptions.LineStopNotFoundException;
import de.hswhameln.timetablemanager.exceptions.NotFoundException;
import de.hswhameln.timetablemanager.repositories.BusStopRepository;
import de.hswhameln.timetablemanager.repositories.LineRepository;
import de.hswhameln.timetablemanager.repositories.LineStopRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Collection;
import java.util.List;

@Service
public class LineStopService {


    private final LineRepository lineRepository;
    private final BusStopRepository busStopRepository;
    private final LineStopRepository lineStopRepository;

    @Autowired
    public LineStopService(LineRepository lineRepository, BusStopRepository busStopRepository, LineStopRepository lineStopRepository) {
        this.lineRepository = lineRepository;
        this.busStopRepository = busStopRepository;
        this.lineStopRepository = lineStopRepository;
    }


    public List<LineStop> getBusStops(long lineId) throws LineNotFoundException {
        if (!this.lineRepository.existsById(lineId)) {
            throw new LineNotFoundException("lineId", lineId);
        }
        return this.lineStopRepository.findByLineIdOrderByIndex(lineId);
    }

    @Transactional
    public void addBusStop(long lineId, long busStopId, Integer secondsToNextStop, int targetIndex) throws LineNotFoundException, BusStopNotFoundException {
        var line = this.lineRepository.findById(lineId).orElseThrow(() -> new LineNotFoundException("lineId", lineId));
        var busStop = this.busStopRepository.findById(busStopId).orElseThrow(() -> new BusStopNotFoundException("busStopId", busStopId));

        LineStop lineStop = new LineStop(secondsToNextStop, line, busStop);
        insertLineStop(line, lineStop, targetIndex);
    }

    @Transactional
    public void removeBusStop(long lineId, long lineStopId) throws LineNotFoundException, LineStopNotFoundException {
        var line = this.lineRepository.findById(lineId).orElseThrow(() -> new LineNotFoundException("lineId", lineId));
        var lineStop = this.lineStopRepository.findById(lineStopId).orElseThrow(() -> new LineStopNotFoundException("lineStopId", lineStopId));
        if (lineStop.getLine().getId() != lineId) {
            throw new LineStopNotFoundException("lineStopId", lineStopId, String.format("It does not exist on line %d, but on line %d.", lineId, lineStop.getLine().getId()));
        }

        removeLineStop(line, lineStop);
    }

    private void insertLineStop(Line line, LineStop lineStop, int targetIndex) {
        List<LineStop> lineStops = line.getLineStops();
        lineStop.setIndex(targetIndex);
        // increment following indices
        lineStops.stream().skip(targetIndex).forEach(LineStop::incrementIndex);
        lineStops.add(targetIndex, lineStop);

        // will perform n update queries, which could be optimised further if necessary
        // optimization does not appear to be worth the cost (yet) since the number of stops per line should be reasonably small
        this.lineRepository.save(line);
    }

    private void removeLineStop(Line line, LineStop lineStop) {
        List<LineStop> lineStops = line.getLineStops();
        int index = lineStops.indexOf(lineStop);
        lineStops.remove(index);

        // decrement all following indices
        lineStops.stream().skip(index).forEach(LineStop::decrementIndex);

        // will perform n update queries, which could be optimised further if necessary
        // optimization does not appear to be worth the cost (yet) since the number of stops per line should be reasonably small
        this.lineRepository.save(line);
    }
}
