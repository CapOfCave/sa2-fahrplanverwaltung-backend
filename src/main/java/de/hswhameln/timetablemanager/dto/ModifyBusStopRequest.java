package de.hswhameln.timetablemanager.dto;

public class ModifyBusStopRequest {

    private String name;

    public ModifyBusStopRequest() {
    }

    public ModifyBusStopRequest(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}