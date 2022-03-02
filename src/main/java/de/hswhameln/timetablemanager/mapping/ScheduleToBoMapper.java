package de.hswhameln.timetablemanager.mapping;

import de.hswhameln.timetablemanager.businessobjects.ScheduleBO;
import de.hswhameln.timetablemanager.entities.BusStop;
import de.hswhameln.timetablemanager.entities.Line;
import de.hswhameln.timetablemanager.entities.LineStop;
import de.hswhameln.timetablemanager.entities.Schedule;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class ScheduleToBoMapper {

    public ScheduleBO enrichWithTargetDestination(Schedule schedule) {
        Optional<BusStop> targetDestination = getTargetDestination(schedule.getLine(), schedule.isReverseDirection());
        return new ScheduleBO(schedule.getId(), schedule.getLine(), schedule.getStartTime(), targetDestination.orElse(null));
    }

    private Optional<BusStop> getTargetDestination(Line line, boolean reverseDirection) {
        List<LineStop> lineStops = line.getLineStops();
        if (lineStops.isEmpty()) {
            return Optional.empty();
        }
        LineStop targetDestination = reverseDirection ? lineStops.get(lineStops.size() - 1) : lineStops.get(0);
        return Optional.of(targetDestination.getBusStop());
    }
}
