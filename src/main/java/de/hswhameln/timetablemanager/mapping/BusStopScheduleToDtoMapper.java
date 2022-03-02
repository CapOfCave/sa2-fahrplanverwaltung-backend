package de.hswhameln.timetablemanager.mapping;

import de.hswhameln.timetablemanager.businessobjects.BusStopScheduleBO;
import de.hswhameln.timetablemanager.businessobjects.BusStopScheduleEntryBO;
import de.hswhameln.timetablemanager.dto.responses.BusStopOverviewDto;
import de.hswhameln.timetablemanager.dto.responses.BusStopScheduleDto;
import de.hswhameln.timetablemanager.dto.responses.BusStopScheduleEntryDto;
import de.hswhameln.timetablemanager.dto.responses.ScheduleOverviewDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BusStopScheduleToDtoMapper {

    private final BusStopToDtoMapper busStopToDtoMapper;
    private final ScheduleToDtoMapper scheduleToDtoMapper;

    @Autowired
    public BusStopScheduleToDtoMapper(BusStopToDtoMapper busStopToDtoMapper, ScheduleToDtoMapper scheduleToDtoMapper) {
        this.busStopToDtoMapper = busStopToDtoMapper;
        this.scheduleToDtoMapper = scheduleToDtoMapper;
    }

    public BusStopScheduleDto mapToBusStopScheduleDto(BusStopScheduleBO busStopSchedule) {
        BusStopOverviewDto busStopOverviewDto = this.busStopToDtoMapper.mapToBusStopOverviewDto(busStopSchedule.getBusStop());
        List<BusStopScheduleEntryDto> scheduleEntries = busStopSchedule.getScheduleEntries()
                .stream()
                .map(this::mapToBusStopScheduleEntryDto)
                .toList();
        return new BusStopScheduleDto(busStopOverviewDto, scheduleEntries);
    }

    private BusStopScheduleEntryDto mapToBusStopScheduleEntryDto(BusStopScheduleEntryBO busStopScheduleEntry) {
        ScheduleOverviewDto scheduleOverviewDto = this.scheduleToDtoMapper.mapToScheduleOverviewDto(busStopScheduleEntry.getSchedule());
        return new BusStopScheduleEntryDto(scheduleOverviewDto, busStopScheduleEntry.getArrival());
    }
}
