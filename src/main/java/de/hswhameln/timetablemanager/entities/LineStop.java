package de.hswhameln.timetablemanager.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class LineStop {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column
    private Long index;

    @Column
    private Integer secondsToNextStop;

    @ManyToOne
    @JoinColumn(name="lineId")
    private Line line;

    @ManyToOne
    @JoinColumn(name="busStopId")
    private BusStop busStop;

    public LineStop() {
    }

    public LineStop(Long index, Integer secondsToNextStop, Line line, BusStop busStop) {
        this.index = index;
        this.secondsToNextStop = secondsToNextStop;
        this.line = line;
        this.busStop = busStop;
    }

    public BusStop getBusStop() {
        return busStop;
    }

    public Long getIndex() {
        return index;
    }

    public Integer getSecondsToNextStop() {
        return secondsToNextStop;
    }
}

