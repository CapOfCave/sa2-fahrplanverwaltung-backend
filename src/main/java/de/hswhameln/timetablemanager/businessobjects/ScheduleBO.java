package de.hswhameln.timetablemanager.businessobjects;

import de.hswhameln.timetablemanager.entities.BusStop;
import de.hswhameln.timetablemanager.entities.Line;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.time.LocalTime;

/**
 * Different to {@link de.hswhameln.timetablemanager.entities.Schedule} since while the Business Object
 * 'ScheduleBO' contains a target destination, for the internal representation it is much wiser to just
 * save the direction in case other stops are added.
 */
public class ScheduleBO {

    private long id;

    private Line line;

    private LocalTime startTime;

    private BusStop finalDestination;

    public ScheduleBO(long id, Line line, LocalTime startTime, BusStop finalDestination) {
        this.id = id;
        this.line = line;
        this.startTime = startTime;
        this.finalDestination = finalDestination;
    }

    public long getId() {
        return id;
    }

    public Line getLine() {
        return line;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public BusStop getFinalDestination() {
        return finalDestination;
    }
}
