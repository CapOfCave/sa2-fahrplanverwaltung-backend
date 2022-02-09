package de.hswhameln.timetablemanager.dto.requests;

public class ModifyLineRequest {

    private String name;

    public ModifyLineRequest() {
    }

    public ModifyLineRequest(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}