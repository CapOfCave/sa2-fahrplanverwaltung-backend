package de.hswhameln.timetablemanager.dto.responses;

import java.time.LocalTime;

public class ScheduleOverviewDto {
    private final long id;
    private final LocalTime startTime;
    private final LineOverviewDto line;
    private final boolean reverseDirection;
    private final BusStopOverviewDto finalStop;

    public ScheduleOverviewDto(long id, LocalTime startTime, LineOverviewDto line, boolean reverseDirection, BusStopOverviewDto finalStop) {
        this.id = id;
        this.startTime = startTime;
        this.line = line;
        this.reverseDirection = reverseDirection;
        this.finalStop = finalStop;
    }

    public long getId() {
        return id;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LineOverviewDto getLine() {
        return line;
    }

    public BusStopOverviewDto getFinalStop() {
        return finalStop;
    }

    public boolean isReverseDirection() {
        return reverseDirection;
    }
}
