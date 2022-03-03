package de.hswhameln.timetablemanager.businessobjects;

import de.hswhameln.timetablemanager.entities.BusStop;

import java.util.Collection;

public class BusStopTimetableBO {
    private BusStop busStop;
    private Collection<BusStopTimetableEntryBO> timetableEntries;

    public BusStopTimetableBO(BusStop busStop, Collection<BusStopTimetableEntryBO> timetableEntries) {
        this.busStop = busStop;
        this.timetableEntries = timetableEntries;
    }

    public BusStop getBusStop() {
        return busStop;
    }

    public Collection<BusStopTimetableEntryBO> getTimetableEntries() {
        return timetableEntries;
    }

    @Override
    public String toString() {
        return "BusStopScheduleBO{" +
                "busStop=" + busStop +
                ", scheduleEntries=" + timetableEntries +
                '}';
    }

}
