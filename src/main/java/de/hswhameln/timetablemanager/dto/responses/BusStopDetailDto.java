package de.hswhameln.timetablemanager.dto.responses;

public class BusStopDetailDto {

    private final long id;
    private final String name;
    // TODO timetables, ...

    public BusStopDetailDto(long id, String name) {
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
