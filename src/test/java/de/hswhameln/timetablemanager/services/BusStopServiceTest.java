package de.hswhameln.timetablemanager.services;

import de.hswhameln.timetablemanager.businessobjects.BusStopSchedulesBO;
import de.hswhameln.timetablemanager.businessobjects.BusStopScheduleEntryBO;
import de.hswhameln.timetablemanager.businessobjects.ScheduleBO;
import de.hswhameln.timetablemanager.entities.BusStop;
import de.hswhameln.timetablemanager.exceptions.BusStopNotFoundException;
import de.hswhameln.timetablemanager.exceptions.DeletionForbiddenException;
import de.hswhameln.timetablemanager.exceptions.NameAlreadyTakenException;
import de.hswhameln.timetablemanager.repositories.BusStopRepository;
import de.hswhameln.timetablemanager.test.SpringAssistedUnitTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

import javax.transaction.Transactional;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BusStopServiceTest extends SpringAssistedUnitTest {

    private final BusStopRepository busStopRepository;

    private final BusStopService objectUnderTest;

    @Autowired
    BusStopServiceTest(BusStopRepository busStopRepository, BusStopService objectUnderTest) {
        this.busStopRepository = busStopRepository;
        this.objectUnderTest = objectUnderTest;
    }

    @Test
    void testCreateBusStop() throws NameAlreadyTakenException {
        long countBefore = busStopRepository.count();
        String name = "5th Avenue";
        BusStop returnedBusStop = this.objectUnderTest.createBusStop(name);

        BusStop actualBusStop = busStopRepository.findById(returnedBusStop.getId()).orElseThrow();
        assertEquals(name, actualBusStop.getName());
        assertEquals(countBefore + 1, busStopRepository.count());
    }

    @Test
    @Sql("/data-test.sql")
    void testCreateBusStopNameTaken() {
        long countBefore = busStopRepository.count();
        assertThrows(NameAlreadyTakenException.class, () -> this.objectUnderTest.createBusStop("Abbey Road"));
        assertEquals(countBefore, busStopRepository.count());

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
    void testGetBusStopsEmpty() {
        Collection<BusStop> busStops = this.objectUnderTest.getBusStops();
        assertTrue(busStops.isEmpty());
    }

    @Test
    @Sql("/data-test.sql")
    void testGetBusStop() throws BusStopNotFoundException {
        BusStop busStop = this.objectUnderTest.getBusStop(1);
        assertEquals("Abbey Road", busStop.getName());
    }

    @Test
    @Sql("/data-test.sql")
    void testGetBusStopNonExistent() {
        assertThrows(BusStopNotFoundException.class, () -> this.objectUnderTest.getBusStop(7777L));
    }

    @Test
    @Sql("/busstops.sql")
    void testDeleteBusStop() throws BusStopNotFoundException, DeletionForbiddenException {
        long targetId = 8;
        long countBefore = busStopRepository.count();
        this.objectUnderTest.deleteBusStop(targetId);
        assertEquals(countBefore - 1, this.busStopRepository.count());
        assertThat(this.busStopRepository.findById(targetId)).isEmpty();
    }

    @Test
    @Sql("/data-test.sql")
    void testDeleteBusStopFailsWithLine() {
        long targetId = 1L;
        long countBefore = busStopRepository.count();
        assertThrows(DeletionForbiddenException.class, () -> this.objectUnderTest.deleteBusStop(targetId));
        assertEquals(countBefore, this.busStopRepository.count());
        assertThat(this.busStopRepository.findById(targetId)).isPresent();
    }

    @Test
    @Sql("/data-test.sql")
    void testDeleteBusStopNonExistent() {
        long targetId = 7777;
        assertThrows(BusStopNotFoundException.class, () -> this.objectUnderTest.deleteBusStop(targetId));
        assertThat(this.busStopRepository.findById(targetId)).isEmpty();
    }

    @Test
    @Sql("/data-test.sql")
    void testModifyBusStop() throws BusStopNotFoundException {
        this.objectUnderTest.modifyBusStop(1L, "Rainy Road");
        BusStop newBusStop = this.busStopRepository.findById(1L).orElseThrow();
        assertEquals("Rainy Road", newBusStop.getName());
    }

    @Test
    @Sql("/data-test.sql")
    void testModifyBusStopNonExistent() {
        assertThrows(BusStopNotFoundException.class, () -> this.objectUnderTest.modifyBusStop(7777L, "Rainy Road"));
    }

    @Test
    @Sql("/data-test.sql")
    void testGetBusScheduleStopNonExistent() {
        assertThrows(BusStopNotFoundException.class, () -> this.objectUnderTest.getBusStopSchedule(7777L));
    }

    @Test
    @Sql("/getRelevantSchedulesData.sql")
    void testGetBusStopScheduleDefaultDirection() throws BusStopNotFoundException {
        BusStopSchedulesBO busStopSchedule = this.objectUnderTest.getBusStopSchedule(4L);
        assertEquals(4L, busStopSchedule.getBusStop().getId());
        assertThat(busStopSchedule.getScheduleEntries())
                .hasSize(1)
                .first().satisfies(busStopScheduleEntryBO -> {
                    assertThat(busStopScheduleEntryBO)
                            .extracting(BusStopScheduleEntryBO::getSchedule)
                            .extracting(
                                    ScheduleBO::getId,
                                    ScheduleBO::getStartTime,
                                    schedule -> schedule.getFinalDestination().getName(),
                                    schedule -> schedule.getLine().getName()
                            )
                            .containsExactly(1L, LocalTime.of(6, 0), "Camp Street", "L1");
                    assertThat(busStopScheduleEntryBO)
                            .extracting(BusStopScheduleEntryBO::getArrival)
                            .isEqualTo(LocalTime.of(6, 1));
                });

    }

    @Test
    @Sql("/getRelevantSchedulesData.sql")
    void testGetBusStopScheduleReverseDirection() throws BusStopNotFoundException {
        long busStopId = 5L;
        BusStopSchedulesBO busStopSchedule = this.objectUnderTest.getBusStopSchedule(busStopId);
        assertEquals(busStopId, busStopSchedule.getBusStop().getId());
        assertThat(busStopSchedule.getScheduleEntries())
                .hasSize(1)
                .first().satisfies(busStopScheduleEntryBO -> {
                    assertThat(busStopScheduleEntryBO)
                            .extracting(BusStopScheduleEntryBO::getSchedule)
                            .extracting(
                                    ScheduleBO::getId,
                                    ScheduleBO::getStartTime,
                                    schedule -> schedule.getFinalDestination().getName(),
                                    schedule -> schedule.getLine().getName()
                            )
                            .containsExactly(2L, LocalTime.of(11, 0), "Abbey Road", "L2");
                    assertThat(busStopScheduleEntryBO)
                            .extracting(BusStopScheduleEntryBO::getArrival)
                            .isEqualTo(LocalTime.of(11, 2));
                });
    }

    @Test
    @Transactional
    @Sql("/schedulesOverMidnight.sql")
    void testGetBusStopScheduleDefaultDirectionCrossingMidnight() throws BusStopNotFoundException {
        BusStopSchedulesBO busStopSchedule = this.objectUnderTest.getBusStopSchedule(4L);
        assertEquals(4L, busStopSchedule.getBusStop().getId());
        assertThat(busStopSchedule.getScheduleEntries())
                .hasSize(1)
                .first().satisfies(busStopScheduleEntryBO -> {
                    assertThat(busStopScheduleEntryBO)
                            .extracting(BusStopScheduleEntryBO::getSchedule)
                            .extracting(
                                    ScheduleBO::getId,
                                    ScheduleBO::getStartTime,
                                    schedule -> schedule.getFinalDestination().getName(),
                                    schedule -> schedule.getLine().getName()
                            )
                            .containsExactly(1L, LocalTime.of(22, 0), "Camp Street", "L1");
                    assertThat(busStopScheduleEntryBO)
                            .extracting(BusStopScheduleEntryBO::getArrival)
                            // 3 AM on the day after the start of the ride (which is irrelevant here)
                            .isEqualTo(LocalTime.of(3, 0));
                });
    }

    @Test
    @Transactional
    @Sql("/schedulesOverMidnight.sql")
    void testGetBusStopScheduleReverseDirectionCrossingMidnight() throws BusStopNotFoundException {
        BusStopSchedulesBO busStopSchedule = this.objectUnderTest.getBusStopSchedule(5L);
        assertEquals(5L, busStopSchedule.getBusStop().getId());
        assertThat(busStopSchedule.getScheduleEntries())
                .hasSize(1)
                .first().satisfies(busStopScheduleEntryBO -> {
                    assertThat(busStopScheduleEntryBO)
                            .extracting(BusStopScheduleEntryBO::getSchedule)
                            .extracting(
                                    ScheduleBO::getId,
                                    ScheduleBO::getStartTime,
                                    schedule -> schedule.getFinalDestination().getName(),
                                    schedule -> schedule.getLine().getName()
                            )
                            .containsExactly(2L, LocalTime.of(21, 30), "Abbey Road", "L2");
                    assertThat(busStopScheduleEntryBO)
                            .extracting(BusStopScheduleEntryBO::getArrival)
                            // 3 AM on the day after the start of the ride (which is irrelevant here)
                            .isEqualTo(LocalTime.of(4, 30));
                });
    }

    @Test
    @Sql("/data-test.sql")
    void testGetBusStopTimetableNonExistent() {
        assertThrows(BusStopNotFoundException.class, () -> this.objectUnderTest.getTimetable(7777L, LocalDateTime.of(2022, 1, 1, 1, 1), Duration.ZERO));
    }
}