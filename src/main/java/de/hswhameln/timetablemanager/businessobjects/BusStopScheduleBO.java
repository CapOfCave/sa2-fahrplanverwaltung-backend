package de.hswhameln.timetablemanager.businessobjects;

import de.hswhameln.timetablemanager.entities.BusStop;

import java.util.Collection;

public class BusStopScheduleBO {
    private BusStop busStop;
    private Collection<BusStopScheduleEntryBO> scheduleEntries;

    public BusStopScheduleBO(BusStop busStop, Collection<BusStopScheduleEntryBO> scheduleEntries) {
        this.busStop = busStop;
        this.scheduleEntries = scheduleEntries;
    }

    public BusStop getBusStop() {
        return busStop;
    }

    public Collection<BusStopScheduleEntryBO> getScheduleEntries() {
        return scheduleEntries;
    }
}
