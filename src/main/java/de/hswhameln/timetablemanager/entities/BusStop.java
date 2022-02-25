package de.hswhameln.timetablemanager.entities;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Entity
public class BusStop {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column
    private String name;

    @OneToMany(mappedBy = "busStop", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LineStop> lineStops = new ArrayList<>();


    public BusStop() {
    }

    public BusStop(String name) {
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
        return lineStops;
    }

    @Override
    public String toString() {
        return "BusStop{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", lineStops=" + lineStops +
                '}';
    }
}
