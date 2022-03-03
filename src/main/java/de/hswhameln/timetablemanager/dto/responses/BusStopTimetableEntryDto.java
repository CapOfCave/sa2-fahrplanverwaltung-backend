package de.hswhameln.timetablemanager.dto.responses;

import java.time.LocalDateTime;

public class BusStopTimetableEntryDto {

    private ScheduleOverviewDto schedule;
    private LocalDateTime arrival;

    public BusStopTimetableEntryDto(ScheduleOverviewDto schedule, LocalDateTime arrival) {
        this.schedule = schedule;
        this.arrival = arrival;
    }

    public ScheduleOverviewDto getSchedule() {
        return schedule;
    }

    public LocalDateTime getArrival() {
        return arrival;
    }
}
