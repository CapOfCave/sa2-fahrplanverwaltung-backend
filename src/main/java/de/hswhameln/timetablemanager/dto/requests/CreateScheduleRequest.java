package de.hswhameln.timetablemanager.dto.requests;

import java.time.LocalTime;

public class CreateScheduleRequest {

    private String lineId;

    private LocalTime startTime;

    private boolean reverseDirection;

    public CreateScheduleRequest() {
    }

    public String getLineId() {
        return lineId;
    }

    public void setLineId(String lineId) {
        this.lineId = lineId;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public boolean isReverseDirection() {
        return reverseDirection;
    }

    public void setReverseDirection(boolean reverseDirection) {
        this.reverseDirection = reverseDirection;
    }
}
