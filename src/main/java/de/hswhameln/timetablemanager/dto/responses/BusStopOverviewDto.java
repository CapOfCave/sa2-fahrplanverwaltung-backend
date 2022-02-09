package de.hswhameln.timetablemanager.dto.responses;

public class BusStopOverviewDto {
    private final long id;
    private final String name;

    public BusStopOverviewDto(long id, String name) {
        this.id = id;
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
