package de.hswhameln.timetablemanager.dto.responses;

import java.util.Collection;

public class BusStopSchedulesDto {

    private BusStopOverviewDto busStop;
    private LineOverviewDto line;

    private Collection<BusStopScheduleEntryDto> scheduleEntries;

    public BusStopSchedulesDto(BusStopOverviewDto busStop, LineOverviewDto line, Collection<BusStopScheduleEntryDto> scheduleEntries) {
        this.busStop = busStop;
        this.line = line;
        this.scheduleEntries = scheduleEntries;
    }

    public BusStopOverviewDto getBusStop() {
        return busStop;
    }

    public Collection<BusStopScheduleEntryDto> getScheduleEntries() {
        return scheduleEntries;
    }

    public LineOverviewDto getLine() {
        return line;
    }
}
