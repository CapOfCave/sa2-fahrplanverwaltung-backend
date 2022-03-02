package de.hswhameln.timetablemanager.businessobjects;

import de.hswhameln.timetablemanager.entities.BusStop;
import de.hswhameln.timetablemanager.entities.Schedule;

import java.time.LocalTime;

public class BusStopScheduleEntryBO {

    private ScheduleBO schedule;
    private LocalTime arrival;

    public BusStopScheduleEntryBO(BusStop busStop, ScheduleBO schedule, LocalTime arrival) {
        this.schedule = schedule;
        this.arrival = arrival;
    }

    public ScheduleBO getSchedule() {
        return schedule;
    }

    public LocalTime getArrival() {
        return arrival;
    }
}
