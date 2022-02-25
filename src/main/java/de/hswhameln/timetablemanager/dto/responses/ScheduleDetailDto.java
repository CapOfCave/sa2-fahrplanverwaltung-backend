package de.hswhameln.timetablemanager.dto.responses;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public class ScheduleDetailDto {
    private long id;
    private LocalTime startTime;
    private LineOverviewDto line;
    private List<ScheduledStop> stops;

    public static class ScheduledStop {
        private BusStopOverviewDto busStop;
        private LocalDateTime arrival;
    }

}
