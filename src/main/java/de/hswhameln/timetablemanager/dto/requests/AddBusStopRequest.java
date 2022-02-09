package de.hswhameln.timetablemanager.dto.requests;

public class AddBusStopRequest {

    private long busStopId;
    private Integer secondsToNextStop;
    private int targetIndex;

    public long getBusStopId() {
        return busStopId;
    }

    public void setBusStopId(long busStopId) {
        this.busStopId = busStopId;
    }

    public Integer getSecondsToNextStop() {
        return secondsToNextStop;
    }

    public void setSecondsToNextStop(Integer secondsToNextStop) {
        this.secondsToNextStop = secondsToNextStop;
    }

    public int getTargetIndex() {
        return targetIndex;
    }

    public void setTargetIndex(int targetIndex) {
        this.targetIndex = targetIndex;
    }
}
