package de.hswhameln.timetablemanager.dto.responses;

import java.util.Collection;

public class BusStopSchedulesDto {

    private BusStopOverviewDto busStop;

    private Collection<BusStopScheduleEntryDto> scheduleEntries;

    public BusStopSchedulesDto(BusStopOverviewDto busStop, Collection<BusStopScheduleEntryDto> scheduleEntries) {
        this.busStop = busStop;
        this.scheduleEntries = scheduleEntries;
    }

    public BusStopOverviewDto getBusStop() {
        return busStop;
    }

    public Collection<BusStopScheduleEntryDto> getScheduleEntries() {
        return scheduleEntries;
    }
}
