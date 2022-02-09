package de.hswhameln.timetablemanager.services;

import de.hswhameln.timetablemanager.entities.Line;
import de.hswhameln.timetablemanager.entities.LineStop;
import de.hswhameln.timetablemanager.exceptions.NotFoundException;
import de.hswhameln.timetablemanager.repositories.BusStopRepository;
import de.hswhameln.timetablemanager.repositories.LineRepository;
import de.hswhameln.timetablemanager.repositories.LineStopRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
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

    public void addBusStop(long lineId, long busStopId, Integer secondsToNextStop, int targetIndex) {
        var line = this.lineRepository.getById(lineId);
        var busStop = this.busStopRepository.getById(busStopId);


        LineStop lineStop = new LineStop(secondsToNextStop, line, busStop);
        insertLineStop(line, lineStop, targetIndex);
    }


    public void removeBusStop(long lineId, long lineStopId) throws NotFoundException {
        var line = this.lineRepository.findById(lineId).orElseThrow(() -> new NotFoundException("Line with ID " + lineId + " does not exist." ));
        var lineStop = this.lineStopRepository.findById(lineStopId).orElseThrow(() -> new NotFoundException("LineStop with ID " + lineStopId + " does not exist." ));
        if (lineStop._getLine().getId() != lineId) {
            throw new NotFoundException("LineStop with ID " + lineStopId + " does not exist on line " + lineId + ". (Hint: it's on line " + lineStop._getLine().getId() +" instead.)");
        }

        removeLineStop(line, lineStop);
    }

    private void insertLineStop(Line line, LineStop lineStop, int targetIndex) {
        List<LineStop> lineStops = line._getLineStops();
        lineStop.setIndex(targetIndex);
        // increment following indices
        lineStops.stream().skip(targetIndex).forEach(LineStop::incrementIndex);
        lineStops.add(targetIndex, lineStop);

        // will perform n update queries, which could be optimised further if necessary
        // optimization does not appear to be worth the cost (yet) since the number of stops per line should be reasonably small
        this.lineRepository.save(line);
    }

    private void removeLineStop(Line line, LineStop lineStop) {
        List<LineStop> lineStops = line._getLineStops();
        int index = lineStops.indexOf(lineStop);
        lineStops.remove(index);

        // decrement all following indices
        lineStops.stream().skip(index).forEach(LineStop::decrementIndex);

        // will perform n update queries, which could be optimised further if necessary
        // optimization does not appear to be worth the cost (yet) since the number of stops per line should be reasonably small
        this.lineRepository.save(line);
    }

    public Collection<LineStop> getBusStops(long id) {
        return this.lineStopRepository.findByLineIdOrderByIndex(id);
    }

    /**
     * Create a new index for a new line stop.
     */
    private int createNewIndex(long lineId) {
        return this.lineStopRepository.countByLineId(lineId);
    }

}
