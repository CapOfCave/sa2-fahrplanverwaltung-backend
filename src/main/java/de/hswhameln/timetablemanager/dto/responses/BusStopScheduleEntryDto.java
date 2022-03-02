package de.hswhameln.timetablemanager.dto.responses;

import java.time.LocalTime;

public class BusStopScheduleEntryDto {

    private ScheduleOverviewDto schedule;
    private LocalTime arrival;

    public BusStopScheduleEntryDto(ScheduleOverviewDto schedule, LocalTime arrival) {
        this.schedule = schedule;
        this.arrival = arrival;
    }

    public ScheduleOverviewDto getSchedule() {
        return schedule;
    }

    public LocalTime getArrival() {
        return arrival;
    }
}
