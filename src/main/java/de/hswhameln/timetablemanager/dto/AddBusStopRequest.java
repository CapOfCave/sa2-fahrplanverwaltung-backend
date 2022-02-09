package de.hswhameln.timetablemanager.dto;

public class AddBusStopRequest {

    private long busStopId;
    private Integer secondsToNextStop;

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
}
