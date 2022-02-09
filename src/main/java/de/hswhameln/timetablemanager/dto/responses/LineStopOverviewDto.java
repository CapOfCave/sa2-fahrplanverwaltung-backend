package de.hswhameln.timetablemanager.dto.responses;

public class LineStopOverviewDto {

    private final long id;

    private final int index;

    private final Integer secondsToNextStop;

    private final long busStopId;

    private final String busStopName;

    public LineStopOverviewDto(long id, int index, Integer secondsToNextStop, long busStopId, String busStopName) {
        this.id = id;
        this.index = index;
        this.secondsToNextStop = secondsToNextStop;
        this.busStopId = busStopId;
        this.busStopName = busStopName;
    }

    public long getId() {
        return id;
    }

    public int getIndex() {
        return index;
    }

    public Integer getSecondsToNextStop() {
        return secondsToNextStop;
    }

    public long getBusStopId() {
        return busStopId;
    }

    public String getBusStopName() {
        return busStopName;
    }
}
