package de.hswhameln.timetablemanager.entities;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Line {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name;

    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("index")
    private List<LineStop> lineStops = new ArrayList<>();

    @OneToMany(mappedBy = "line", fetch = FetchType.LAZY)
    private List<Schedule> schedules = new ArrayList<>();


    public Line() {
    }

    public Line(String name) {
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(long uid) {
        this.id = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<LineStop> getLineStops() {
        return this.lineStops;
    }

    public void setLineStops(List<LineStop> lineStops) {
        this.lineStops = lineStops;
    }

    @Override
    public String toString() {
        return "Line{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", lineStops=" + lineStops +
                '}';
    }

    public List<Schedule> getSchedules() {
        return schedules;
    }
}
