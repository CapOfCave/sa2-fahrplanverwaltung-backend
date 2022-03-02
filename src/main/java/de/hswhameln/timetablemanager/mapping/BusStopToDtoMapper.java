package de.hswhameln.timetablemanager.mapping;

import de.hswhameln.timetablemanager.businessobjects.BusStopScheduleEntryBO;
import de.hswhameln.timetablemanager.dto.responses.BusStopDetailDto;
import de.hswhameln.timetablemanager.dto.responses.BusStopOverviewDto;
import de.hswhameln.timetablemanager.dto.responses.BusStopScheduleEntryDto;
import de.hswhameln.timetablemanager.dto.responses.LineOverviewDto;
import de.hswhameln.timetablemanager.dto.responses.ScheduleOverviewDto;
import de.hswhameln.timetablemanager.entities.BusStop;
import de.hswhameln.timetablemanager.entities.LineStop;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.List;

@Component
public class BusStopToDtoMapper {

    private final LineToDtoMapper lineToDtoMapper;

    @Autowired
    public BusStopToDtoMapper(LineToDtoMapper lineToDtoMapper) {
        this.lineToDtoMapper = lineToDtoMapper;
    }

    public BusStopOverviewDto mapToBusStopOverviewDto(BusStop busStop) {
        return new BusStopOverviewDto(busStop.getId(), busStop.getName());
    }

    @Transactional
    public BusStopDetailDto mapToBusStopDetailDto(BusStop busStop) {
        List<LineOverviewDto> lines = busStop.getLineStops()
                .stream()
                .map(LineStop::getLine)
                .distinct()
                .map(this.lineToDtoMapper::mapToLineOverviewDto)
                .toList();
        return new BusStopDetailDto(busStop.getId(), busStop.getName(), lines);
    }
}
