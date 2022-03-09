package de.hswhameln.timetablemanager.mapping;

import de.hswhameln.timetablemanager.businessobjects.BusStopSchedulesBO;
import de.hswhameln.timetablemanager.businessobjects.BusStopScheduleEntryBO;
import de.hswhameln.timetablemanager.businessobjects.BusStopTimetableBO;
import de.hswhameln.timetablemanager.businessobjects.BusStopTimetableEntryBO;
import de.hswhameln.timetablemanager.dto.responses.BusStopOverviewDto;
import de.hswhameln.timetablemanager.dto.responses.BusStopSchedulesDto;
import de.hswhameln.timetablemanager.dto.responses.BusStopScheduleEntryDto;
import de.hswhameln.timetablemanager.dto.responses.BusStopTimetableDto;
import de.hswhameln.timetablemanager.dto.responses.BusStopTimetableEntryDto;
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

    public BusStopSchedulesDto mapToBusStopSchedulesDto(BusStopSchedulesBO busStopSchedule) {
        BusStopOverviewDto busStopOverviewDto = this.busStopToDtoMapper.mapToBusStopOverviewDto(busStopSchedule.getBusStop());
        List<BusStopScheduleEntryDto> scheduleEntries = busStopSchedule.getScheduleEntries()
                .stream()
                .map(this::mapToBusStopScheduleEntryDto)
                .toList();
        return new BusStopSchedulesDto(busStopOverviewDto, scheduleEntries);
    }

    private BusStopScheduleEntryDto mapToBusStopScheduleEntryDto(BusStopScheduleEntryBO busStopScheduleEntry) {
        ScheduleOverviewDto scheduleOverviewDto = this.scheduleToDtoMapper.mapToScheduleOverviewDto(busStopScheduleEntry.getSchedule());
        return new BusStopScheduleEntryDto(scheduleOverviewDto, busStopScheduleEntry.getArrival());
    }

    public BusStopTimetableDto mapToBusStopTimetableDto(BusStopTimetableBO busStopTimetable) {
        BusStopOverviewDto busStopOverviewDto = this.busStopToDtoMapper.mapToBusStopOverviewDto(busStopTimetable.getBusStop());
        List<BusStopTimetableEntryDto> timetableEntries = busStopTimetable.getTimetableEntries()
                .stream()
                .map(this::mapToBusStopTimetableEntryDto)
                .toList();
        return new BusStopTimetableDto(busStopOverviewDto, timetableEntries);
    }

    private BusStopTimetableEntryDto mapToBusStopTimetableEntryDto(BusStopTimetableEntryBO busStopTimetableEntry) {
        ScheduleOverviewDto scheduleOverviewDto = this.scheduleToDtoMapper.mapToScheduleOverviewDto(busStopTimetableEntry.getScheduleBO());
        return new BusStopTimetableEntryDto(scheduleOverviewDto, busStopTimetableEntry.getArrival());
    }
}
