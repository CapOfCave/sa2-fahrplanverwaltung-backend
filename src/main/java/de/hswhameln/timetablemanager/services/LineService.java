package de.hswhameln.timetablemanager.services;

import de.hswhameln.timetablemanager.entities.Line;
import de.hswhameln.timetablemanager.repositories.LineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
public class LineService {

    private final LineRepository lineRepository;

    @Autowired
    public LineService(LineRepository lineRepository) {
        this.lineRepository = lineRepository;
    }

    public Line createLine(String name) {
        // TODO check if name is unique
        var line = new Line(name);
        return this.lineRepository.save(line);
    }

    public Collection<Line> getLines() {
        List<Line> lines = new ArrayList<>();
        this.lineRepository.findAll().forEach(lines::add);
        return lines;
    }

    public void deleteLine(long id) {
        this.lineRepository.deleteById(id);
    }

    public Line modifyLine(long id, String name) {
        Line line = this.lineRepository.findById(id).orElseThrow();
        line.setName(name);
        return this.lineRepository.save(line);
    }
}
