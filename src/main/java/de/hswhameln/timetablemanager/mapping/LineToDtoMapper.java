package de.hswhameln.timetablemanager.mapping;

import de.hswhameln.timetablemanager.dto.responses.LineDetailDto;
import de.hswhameln.timetablemanager.dto.responses.LineOverviewDto;
import de.hswhameln.timetablemanager.dto.responses.LineStopOverviewDto;
import de.hswhameln.timetablemanager.entities.Line;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class LineToDtoMapper {

    private final LineStopToDtoMapper lineStopToDtoMapper;

    @Autowired
    public LineToDtoMapper(LineStopToDtoMapper lineStopToDtoMapper) {
        this.lineStopToDtoMapper = lineStopToDtoMapper;
    }

    public LineOverviewDto mapToLineOverviewDto(Line line) {
        return new LineOverviewDto(line.getId(), line.getName());
    }

    public LineDetailDto mapToLineDetailDto(Line line) {
        List<LineStopOverviewDto> lineStops = line._getLineStops()
                .stream()
                .map(lineStopToDtoMapper::mapToLineStopOverviewDto)
                .toList();
        return new LineDetailDto(line.getId(), line.getName(), lineStops);
    }
}
