package de.hswhameln.timetablemanager.dto;

public class CreateLineRequest {

    private String name;

    public CreateLineRequest() {
    }

    public CreateLineRequest(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
