package de.hswhameln.timetablemanager.dto.responses;

import java.util.List;

public class LineDetailDto {

    private final long id;
    private final String name;
    private final List<LineStopOverviewDto> lineStops;


    public LineDetailDto(long id, String name, List<LineStopOverviewDto> lineStops) {
        this.id = id;
        this.name = name;
        this.lineStops = lineStops;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<LineStopOverviewDto> getLineStops() {
        return lineStops;
    }
}
