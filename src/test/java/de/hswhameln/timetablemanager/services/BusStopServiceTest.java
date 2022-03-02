package de.hswhameln.timetablemanager.services;

import de.hswhameln.timetablemanager.businessobjects.BusStopScheduleBO;
import de.hswhameln.timetablemanager.businessobjects.BusStopScheduleEntryBO;
import de.hswhameln.timetablemanager.businessobjects.ScheduleBO;
import de.hswhameln.timetablemanager.entities.BusStop;
import de.hswhameln.timetablemanager.repositories.BusStopRepository;
import de.hswhameln.timetablemanager.test.UnitTest;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

class BusStopServiceTest extends UnitTest {

    private final BusStopRepository busStopRepository;

    private final BusStopService objectUnderTest;

    @Autowired
    BusStopServiceTest(BusStopRepository busStopRepository, BusStopService objectUnderTest) {
        this.busStopRepository = busStopRepository;
        this.objectUnderTest = objectUnderTest;
    }

    @Test
    void testCreateBusStop() {
        long countBefore = busStopRepository.count();
        String name = "5th Avenue";
        BusStop returnedBusStop = this.objectUnderTest.createBusStop(name);

        BusStop actualBusStop = busStopRepository.findById(returnedBusStop.getId()).orElseThrow();
        assertEquals(name, actualBusStop.getName());
        assertEquals(countBefore + 1, busStopRepository.count());
    }

    @Test
    @Sql("/data-test.sql")
    void testGetBusStops() {
        int count = (int) this.busStopRepository.count();
        Collection<BusStop> busStops = this.objectUnderTest.getBusStops();
        assertThat(busStops)
                .hasSizeGreaterThan(1)
                .hasSize(count);
    }

    @Test
    @Sql("/data-test.sql")
    void testGetBusStop() {
        BusStop busStop = this.objectUnderTest.getBusStop(1).orElseThrow();
        assertEquals("Abbey Road", busStop.getName());
    }

    @Test
    @Sql("/data-test.sql")
    void testDeleteBusStop() {
        long targetId = 8;
        long countBefore = busStopRepository.count();
        this.objectUnderTest.deleteBusStop(targetId);
        assertEquals(countBefore - 1, this.busStopRepository.count());
        assertThat(this.busStopRepository.findById(targetId)).isEmpty();
    }

    @Test
    @Sql("/data-test.sql")
    void testModifyBusStop() {
        this.objectUnderTest.modifyBusStop(1L, "Rainy Road");
        BusStop newBusStop = this.busStopRepository.findById(1L).orElseThrow();
        assertEquals("Rainy Road", newBusStop.getName());
    }

    @Test
    @Sql("/getRelevantSchedulesData.sql")
    void testGetBusStopScheduleDefaultDirection() {
        BusStopScheduleBO busStopSchedule = this.objectUnderTest.getBusStopSchedule(4L);
        assertEquals(4L, busStopSchedule.getBusStop().getId());
        assertThat(busStopSchedule.getScheduleEntries()).hasSize(1).first().satisfies(scheduleEntry -> {
            assertThat(scheduleEntry)
                    .extracting(BusStopScheduleEntryBO::getSchedule)
                    .extracting(
                            ScheduleBO::getId,
                            ScheduleBO::getStartTime,
                            schedule -> schedule.getFinalDestination().getName(),
                            schedule -> schedule.getLine().getName()
                    )
                    .containsExactly(1L, LocalTime.of(6, 0), "Camp Street", "L1");
        });
    }

    @Test
    @Sql("/getRelevantSchedulesData.sql")
    void testGetBusStopScheduleReverseDirection() {
        long busStopId = 5L;
        BusStopScheduleBO busStopSchedule = this.objectUnderTest.getBusStopSchedule(busStopId);
        assertEquals(busStopId, busStopSchedule.getBusStop().getId());
        assertThat(busStopSchedule.getScheduleEntries()).hasSize(1).first().satisfies(scheduleEntry -> {
            assertThat(scheduleEntry)
                    .extracting(BusStopScheduleEntryBO::getSchedule)
                    .extracting(
                            ScheduleBO::getId,
                            ScheduleBO::getStartTime,
                            schedule -> schedule.getFinalDestination().getName(),
                            schedule -> schedule.getLine().getName()
                    )
                    .containsExactly(2L, LocalTime.of(11, 0), "Abbey Road", "L2");
        });
    }
}