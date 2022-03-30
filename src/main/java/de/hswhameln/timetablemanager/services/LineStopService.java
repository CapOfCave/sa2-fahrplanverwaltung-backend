package de.hswhameln.timetablemanager.services;

import de.hswhameln.timetablemanager.entities.Line;
import de.hswhameln.timetablemanager.entities.LineStop;
import de.hswhameln.timetablemanager.exceptions.BusStopNotFoundException;
import de.hswhameln.timetablemanager.exceptions.InvalidArgumentException;
import de.hswhameln.timetablemanager.exceptions.LineNotFoundException;
import de.hswhameln.timetablemanager.exceptions.LineStopNotFoundException;
import de.hswhameln.timetablemanager.repositories.BusStopRepository;
import de.hswhameln.timetablemanager.repositories.LineRepository;
import de.hswhameln.timetablemanager.repositories.LineStopRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
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
            throw new LineNotFoundException("ID", lineId);
        }
        return this.lineStopRepository.findByLineIdOrderByIndex(lineId);
    }

    @Transactional
    public void addBusStop(long lineId, long busStopId, Integer secondsToNextStop, int targetIndex) throws LineNotFoundException, BusStopNotFoundException, InvalidArgumentException {
        var line = getLineById(lineId);
        var busStop = this.busStopRepository.findById(busStopId).orElseThrow(() -> new BusStopNotFoundException("ID", busStopId));
        validateTargetIndex(targetIndex, line, true);

        LineStop lineStop = new LineStop(secondsToNextStop, line, busStop);
        insertLineStop(line, lineStop, targetIndex);
    }


    @Transactional
    public void removeBusStop(long lineId, long lineStopId) throws LineNotFoundException, LineStopNotFoundException {
        var line = getLineById(lineId);
        var lineStop = getLineStopById(lineStopId);
        validateLineStopLineId(lineId, lineStop);

        removeLineStop(line, lineStop);
    }


    @Transactional
    public LineStop modifyLineStop(long lineId, long lineStopId, Integer targetIndex, Integer secondsToNextStop) throws LineNotFoundException, LineStopNotFoundException, InvalidArgumentException {
        Line line = getLineById(lineId);
        LineStop lineStop = getLineStopById(lineStopId);
        validateLineStopLineId(lineId, lineStop);

        if (targetIndex != null && targetIndex != lineStop.getIndex()) {
            validateTargetIndex(targetIndex, line, false);
            removeLineStop(line, lineStop);
            insertLineStop(line, lineStop, targetIndex);
        }
        if (secondsToNextStop != null) {
            lineStop.setSecondsToNextStop(secondsToNextStop);
            this.lineStopRepository.save(lineStop);
        }

        return getLineStopById(lineStopId);
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

    private Line getLineById(long lineId) throws LineNotFoundException {
        return this.lineRepository.findById(lineId).orElseThrow(() -> new LineNotFoundException("ID", lineId));
    }

    private LineStop getLineStopById(long lineStopId) throws LineStopNotFoundException {
        return this.lineStopRepository.findById(lineStopId).orElseThrow(() -> new LineStopNotFoundException("ID", lineStopId));
    }

    private void validateLineStopLineId(long lineId, LineStop lineStop) throws LineStopNotFoundException {
        if (lineStop.getLine().getId() != lineId) {
            throw new LineStopNotFoundException("ID", lineStop.getId(), String.format("Er existiert nicht auf Buslinie %d, sondern auf Buslinie %d.", lineId, lineStop.getLine().getId()));
        }
    }
    private void validateTargetIndex(Integer targetIndex, Line line, boolean allowAppending) throws InvalidArgumentException {
        if (targetIndex < 0 ) {
            throw new InvalidArgumentException("Ziel-Index", targetIndex, "Er muss größer als 0 sein.");
        }
        int maxIndex = line.getLineStops().size() - (allowAppending ? 0 : 1);
        String formatString = allowAppending ? "Er darf nicht größer als die Anzahl an der Halte auf dieser Linie, die %d beträgt."
                : "Er muss kleiner sein als die Anzahl der Halte auf dieser Linie, die %d beträgt.";

        if (targetIndex > maxIndex) {
            throw new InvalidArgumentException("Ziel-Index", targetIndex, String.format(formatString, line.getLineStops().size()));
        }
    }

}
