package de.hswhameln.timetablemanager.mapping;

import de.hswhameln.timetablemanager.dto.responses.BusStopDetailDto;
import de.hswhameln.timetablemanager.dto.responses.BusStopOverviewDto;
import de.hswhameln.timetablemanager.entities.BusStop;
import org.springframework.stereotype.Component;

@Component
public class BusStopToDtoMapper {


    public BusStopOverviewDto mapToBusStopOverviewDto(BusStop busStop) {
        return new BusStopOverviewDto(busStop.getId(), busStop.getName());
    }

    public BusStopDetailDto mapToBusStopDetailDto(BusStop busStop) {
        // TODO enrich with detailed information
        return new BusStopDetailDto(busStop.getId(), busStop.getName());
    }
}
