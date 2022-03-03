package de.hswhameln.timetablemanager.businessobjects;

import java.time.LocalDateTime;

public class BusStopTimetableEntryBO {

    private ScheduleBO scheduleBO;
    private LocalDateTime arrival;

    public BusStopTimetableEntryBO(ScheduleBO scheduleBO, LocalDateTime arrival) {
        this.scheduleBO = scheduleBO;
        this.arrival = arrival;
    }

    public ScheduleBO getScheduleBO() {
        return scheduleBO;
    }

    public LocalDateTime getArrival() {
        return arrival;
    }
}
