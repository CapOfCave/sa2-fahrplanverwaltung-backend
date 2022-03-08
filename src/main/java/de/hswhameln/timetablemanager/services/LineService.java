package de.hswhameln.timetablemanager.services;

import de.hswhameln.timetablemanager.entities.Line;
import de.hswhameln.timetablemanager.exceptions.DeletionForbiddenException;
import de.hswhameln.timetablemanager.exceptions.LineNotFoundException;
import de.hswhameln.timetablemanager.exceptions.NameAlreadyTakenException;
import de.hswhameln.timetablemanager.repositories.LineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Collection;

@Service
public class LineService {

    private final LineRepository lineRepository;

    @Autowired
    public LineService(LineRepository lineRepository) {
        this.lineRepository = lineRepository;
    }

    public Line createLine(String name) throws NameAlreadyTakenException {
        if (this.lineRepository.existsByName(name)) {
            throw new NameAlreadyTakenException(name);
        }
        var line = new Line(name);
        return this.lineRepository.save(line);
    }

    public Collection<Line> getLines() {
        return new ArrayList<>(this.lineRepository.findAll());
    }

    @Transactional
    public void deleteLine(long id) throws LineNotFoundException, DeletionForbiddenException {
        Line line = getLine(id);
        if (!line.getSchedules().isEmpty()) {
            throw new DeletionForbiddenException("Line", id, "This Line is part of at least one Schedule.");
        }
        this.lineRepository.deleteById(id);
    }

    public Line modifyLine(long id, String name) throws LineNotFoundException {
        Line line = getLine(id);
        line.setName(name);
        return this.lineRepository.save(line);
    }

    public Line getLine(long id) throws LineNotFoundException {
        return this.lineRepository.findById(id).orElseThrow(() -> new LineNotFoundException("lineId", id));
    }

}
