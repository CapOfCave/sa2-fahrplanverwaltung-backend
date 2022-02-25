package de.hswhameln.timetablemanager.dto.responses;

import java.time.LocalDateTime;

public class ScheduleOverviewDto {
    private long id;
    private LocalDateTime startTime;
    private LineOverviewDto line;
    private BusStopOverviewDto finalStop;
}
