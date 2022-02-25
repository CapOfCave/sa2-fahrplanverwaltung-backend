package de.hswhameln.timetablemanager.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.time.LocalTime;

@Entity
public class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "lineId")
    private Line line;

    @Column
    private LocalTime startTime;

    /**
     * Internally save the direction, not the target destination to ensure normality and data integrity (accepting a slight performance cost on read operations)
     */
    @Column
    private boolean reverseDirection;

    public Schedule() {
    }

    public Schedule(Line line, LocalTime startTime, boolean reverseDirection) {
        this.line = line;
        this.startTime = startTime;
        this.reverseDirection = reverseDirection;
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

    public boolean isReverseDirection() {
        return reverseDirection;
    }
}
