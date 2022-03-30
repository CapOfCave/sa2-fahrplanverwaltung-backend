package de.hswhameln.timetablemanager.businessobjects;

import de.hswhameln.timetablemanager.entities.BusStop;
import de.hswhameln.timetablemanager.entities.Line;

import java.util.Collection;

public class BusStopSchedulesBO {
    private BusStop busStop;
    private Line line;
    private Collection<BusStopScheduleEntryBO> scheduleEntries;

    public BusStopSchedulesBO(BusStop busStop, Line line, Collection<BusStopScheduleEntryBO> scheduleEntries) {
        this.busStop = busStop;
        this.line = line;
        this.scheduleEntries = scheduleEntries;
    }

    public BusStop getBusStop() {
        return busStop;
    }

    public Collection<BusStopScheduleEntryBO> getScheduleEntries() {
        return scheduleEntries;
    }

    @Override
    public String toString() {
        return "BusStopScheduleBO{" +
                "busStop=" + busStop +
                ", scheduleEntries=" + scheduleEntries +
                '}';
    }

    public Line getLine() {
        return line;
    }
}
