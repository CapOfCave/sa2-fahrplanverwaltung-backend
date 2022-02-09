package de.hswhameln.timetablemanager.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.util.Collection;

@Entity
public class Line {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name;

    @OneToMany(mappedBy = "line")
    private Collection<LineStop> lineStops;

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

}
