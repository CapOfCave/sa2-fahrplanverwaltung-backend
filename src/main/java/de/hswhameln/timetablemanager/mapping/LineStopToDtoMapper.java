package de.hswhameln.timetablemanager.mapping;

import de.hswhameln.timetablemanager.dto.responses.LineStopOverviewDto;
import de.hswhameln.timetablemanager.entities.LineStop;
import org.springframework.stereotype.Component;

@Component
public class LineStopToDtoMapper {

    public LineStopOverviewDto mapToLineStopOverviewDto(LineStop lineStop) {
        return new LineStopOverviewDto(
                lineStop.getId(),
                lineStop.getSecondsToNextStop(),
                lineStop.getBusStop().getId(),
                lineStop.getBusStop().getName());
    }
}
