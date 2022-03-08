package de.hswhameln.timetablemanager.dto.requests;

import java.time.LocalTime;

public class ModifyScheduleRequest {
    private LocalTime startTime;
    private Boolean reverseDirection;

    public ModifyScheduleRequest() {
    }

    public ModifyScheduleRequest(LocalTime startTime, boolean reverseDirection) {
        this.startTime = startTime;
        this.reverseDirection = reverseDirection;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public Boolean getReverseDirection() {
        return reverseDirection;
    }
}
