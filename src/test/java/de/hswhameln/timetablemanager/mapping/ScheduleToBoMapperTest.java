package de.hswhameln.timetablemanager.mapping;

import de.hswhameln.timetablemanager.businessobjects.ScheduleBO;
import de.hswhameln.timetablemanager.entities.BusStop;
import de.hswhameln.timetablemanager.entities.Line;
import de.hswhameln.timetablemanager.entities.LineStop;
import de.hswhameln.timetablemanager.entities.Schedule;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

class ScheduleToBoMapperTest {

    private final ScheduleToBoMapper objectUnderTest;

    ScheduleToBoMapperTest() {
        this.objectUnderTest = new ScheduleToBoMapper();
    }

    @Test
    void testEnrichWithTargetDestinationEmptyLine() {
        Line line = new Line("lineName");
        LocalTime startTime = LocalTime.of(6, 0);

        Schedule schedule = new Schedule(line, startTime, false);
        ScheduleBO actualScheduleBO = this.objectUnderTest.enrichWithTargetDestination(schedule);
        assertSame(line, actualScheduleBO.getLine());
        assertNull(actualScheduleBO.getFinalDestination());

    }

    @Test
    void testEnrichWithTargetDestinationDefaultDirection() {
        Line line = new Line("lineName");

        BusStop busStop1 = new BusStop("b1");
        BusStop busStop2 = new BusStop("b2");
        BusStop busStop3 = new BusStop("b3");

        LineStop lineStop1 = new LineStop(1, 100, line, busStop1);
        LineStop lineStop2 = new LineStop(1, 200, line, busStop2);
        LineStop lineStop3 = new LineStop(1, 400, line, busStop3);
        List<LineStop> lineStops = List.of(lineStop1, lineStop2, lineStop3);
        line.setLineStops(lineStops);

        LocalTime startTime = LocalTime.of(6, 0);

        Schedule schedule = new Schedule(line, startTime, false);
        ScheduleBO actualScheduleBO = this.objectUnderTest.enrichWithTargetDestination(schedule);
        assertSame(line, actualScheduleBO.getLine());
        assertSame(busStop3, actualScheduleBO.getFinalDestination());

    }

    @Test
    void testEnrichWithTargetDestinationReverseDirection() {
        Line line = new Line("lineName");

        BusStop busStop1 = new BusStop("b1");
        BusStop busStop2 = new BusStop("b2");
        BusStop busStop3 = new BusStop("b3");

        LineStop lineStop1 = new LineStop(1, 100, line, busStop1);
        LineStop lineStop2 = new LineStop(1, 200, line, busStop2);
        LineStop lineStop3 = new LineStop(1, 400, line, busStop3);
        List<LineStop> lineStops = List.of(lineStop1, lineStop2, lineStop3);
        line.setLineStops(lineStops);

        LocalTime startTime = LocalTime.of(6, 0);

        Schedule schedule = new Schedule(line, startTime, true);
        ScheduleBO actualScheduleBO = this.objectUnderTest.enrichWithTargetDestination(schedule);
        assertSame(line, actualScheduleBO.getLine());
        assertSame(busStop1, actualScheduleBO.getFinalDestination());

    }
}