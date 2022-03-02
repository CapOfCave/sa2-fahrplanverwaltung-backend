package de.hswhameln.timetablemanager.dto.responses;

import java.util.Collection;

public class BusStopScheduleDto {

    private BusStopOverviewDto busStop;

    private Collection<BusStopScheduleEntryDto> scheduleEntries;

    public BusStopScheduleDto(BusStopOverviewDto busStop, Collection<BusStopScheduleEntryDto> scheduleEntries) {
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
