package de.hswhameln.timetablemanager.dto.requests;

public class ModifyLineStopRequest {

    private Integer secondsToNextStop;
    private Integer targetIndex;

    public Integer getSecondsToNextStop() {
        return secondsToNextStop;
    }

    public void setSecondsToNextStop(Integer secondsToNextStop) {
        this.secondsToNextStop = secondsToNextStop;
    }

    public Integer getTargetIndex() {
        return targetIndex;
    }

    public void setTargetIndex(int targetIndex) {
        this.targetIndex = targetIndex;
    }
}
