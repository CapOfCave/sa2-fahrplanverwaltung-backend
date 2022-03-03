package de.hswhameln.timetablemanager.dto.responses;

import java.util.Collection;

public class BusStopTimetableDto {

    private BusStopOverviewDto busStop;

    private Collection<BusStopTimetableEntryDto> scheduleEntries;

    public BusStopTimetableDto(BusStopOverviewDto busStop, Collection<BusStopTimetableEntryDto> scheduleEntries) {
        this.busStop = busStop;
        this.scheduleEntries = scheduleEntries;
    }

    public BusStopOverviewDto getBusStop() {
        return busStop;
    }

    public Collection<BusStopTimetableEntryDto> getScheduleEntries() {
        return scheduleEntries;
    }
}
