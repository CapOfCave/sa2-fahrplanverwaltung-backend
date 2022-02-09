package de.hswhameln.timetablemanager.dto.responses;

import java.util.Collection;

public class BusStopDetailDto {

    private final long id;
    private final String name;
    private final Collection<LineOverviewDto> lines;
    // TODO timetables, ...

    public BusStopDetailDto(long id, String name, Collection<LineOverviewDto> lines) {
        this.id = id;
        this.name = name;
        this.lines = lines;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }


    public Collection<LineOverviewDto> getLines() {
        return lines;
    }
}
