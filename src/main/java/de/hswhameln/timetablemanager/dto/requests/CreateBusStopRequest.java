package de.hswhameln.timetablemanager.dto.requests;

public class CreateBusStopRequest {

    private String name;

    public CreateBusStopRequest() {
    }

    public CreateBusStopRequest(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
