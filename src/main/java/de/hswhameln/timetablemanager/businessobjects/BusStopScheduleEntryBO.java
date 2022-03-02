package de.hswhameln.timetablemanager.businessobjects;

import java.time.LocalTime;

public class BusStopScheduleEntryBO {

    private ScheduleBO schedule;
    private LocalTime arrival;

    public BusStopScheduleEntryBO(ScheduleBO schedule, LocalTime arrival) {
        this.schedule = schedule;
        this.arrival = arrival;
    }

    public ScheduleBO getSchedule() {
        return schedule;
    }

    public LocalTime getArrival() {
        return arrival;
    }

    @Override
    public String toString() {
        return "BusStopScheduleEntryBO{" +
                "schedule=" + schedule +
                ", arrival=" + arrival +
                '}';
    }


}
