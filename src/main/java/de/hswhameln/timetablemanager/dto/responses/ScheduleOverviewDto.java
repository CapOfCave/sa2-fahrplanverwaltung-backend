package de.hswhameln.timetablemanager.dto.responses;

import java.time.LocalTime;

public class ScheduleOverviewDto {
    private long id;
    private LocalTime startTime;
    private LineOverviewDto line;
    private BusStopOverviewDto finalStop;

    public ScheduleOverviewDto(long id, LocalTime startTime, LineOverviewDto line, BusStopOverviewDto finalStop) {
        this.id = id;
        this.startTime = startTime;
        this.line = line;
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
}
