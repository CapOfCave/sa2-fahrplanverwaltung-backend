package de.hswhameln.timetablemanager.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.util.Objects;

@Entity
public class LineStop {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column
    private int index;

    @Column
    private Integer secondsToNextStop;

    @ManyToOne
    @JoinColumn(name = "lineId")
    private Line line;

    @ManyToOne
    @JoinColumn(name = "busStopId")
    private BusStop busStop;

    public LineStop() {
    }

    public LineStop(int index, Integer secondsToNextStop, Line line, BusStop busStop) {
        this.index = index;
        this.secondsToNextStop = secondsToNextStop;
        this.line = line;
        this.busStop = busStop;
    }

    public LineStop(Integer secondsToNextStop, Line line, BusStop busStop) {
        this.secondsToNextStop = secondsToNextStop;
        this.line = line;
        this.busStop = busStop;
    }

    public BusStop getBusStop() {
        return busStop;
    }

    public Integer getSecondsToNextStop() {
        return secondsToNextStop;
    }

    public void incrementIndex() {
        this.setIndex(this.getIndex() + 1);
    }

    public void decrementIndex() {
        this.setIndex(this.getIndex() - 1);
    }

    public int getIndex() {
        return this.index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public long getId() {
        return id;
    }

    public void setSecondsToNextStop(Integer secondsToNextStop) {
        this.secondsToNextStop = secondsToNextStop;
    }

    @Override
    public String toString() {
        return "LineStop{" +
                "id=" + id +
                ", index=" + index +
                ", secondsToNextStop=" + secondsToNextStop +
                ", line=" + line.getId() +
                ", busStop=" + busStop.getId() +
                '}';
    }

    public Line getLine() {
        return this.line;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LineStop lineStop = (LineStop) o;
        return id == lineStop.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

