package de.hswhameln.timetablemanager.mapping;

import de.hswhameln.timetablemanager.businessobjects.ScheduleBO;
import de.hswhameln.timetablemanager.dto.responses.BusStopOverviewDto;
import de.hswhameln.timetablemanager.dto.responses.LineOverviewDto;
import de.hswhameln.timetablemanager.dto.responses.ScheduleOverviewDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ScheduleToDtoMapper {

    private final LineToDtoMapper lineToDtoMapper;
    private final BusStopToDtoMapper busStopToDtoMapper;

    @Autowired
    public ScheduleToDtoMapper(LineToDtoMapper lineToDtoMapper, BusStopToDtoMapper busStopToDtoMapper) {
        this.lineToDtoMapper = lineToDtoMapper;
        this.busStopToDtoMapper = busStopToDtoMapper;
    }

    public ScheduleOverviewDto mapToScheduleOverviewDto(ScheduleBO schedule) {
        LineOverviewDto lineOverviewDto = this.lineToDtoMapper.mapToLineOverviewDto(schedule.getLine());
        BusStopOverviewDto busStopOverviewDto = this.busStopToDtoMapper.mapToBusStopOverviewDto(schedule.getFinalDestination());

        return new ScheduleOverviewDto(schedule.getId(), schedule.getStartTime(), lineOverviewDto, schedule.isReverseDirection(), busStopOverviewDto );

    }
}
