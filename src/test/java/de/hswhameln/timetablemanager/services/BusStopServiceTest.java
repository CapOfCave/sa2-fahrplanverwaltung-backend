package de.hswhameln.timetablemanager.services;

import de.hswhameln.timetablemanager.businessobjects.BusStopScheduleEntryBO;
import de.hswhameln.timetablemanager.businessobjects.BusStopSchedulesBO;
import de.hswhameln.timetablemanager.businessobjects.BusStopTimetableBO;
import de.hswhameln.timetablemanager.businessobjects.BusStopTimetableEntryBO;
import de.hswhameln.timetablemanager.businessobjects.ScheduleBO;
import de.hswhameln.timetablemanager.entities.BusStop;
import de.hswhameln.timetablemanager.exceptions.BusStopNotFoundException;
import de.hswhameln.timetablemanager.exceptions.DeletionForbiddenException;
import de.hswhameln.timetablemanager.exceptions.LineNotFoundException;
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
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
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
    void testGetBusStopTimetableNonExistent() {
        assertThrows(BusStopNotFoundException.class, () -> this.objectUnderTest.getTimetable(7777L, LocalDateTime.of(2022, 1, 1, 1, 1), Duration.ZERO));
    }

    @Test
    @Sql("/getRelevantSchedulesData.sql")
    void testGetBusStopTimetableOneScheduleDefaultDirection() throws BusStopNotFoundException {
        BusStopTimetableBO timetable = this.objectUnderTest.getTimetable(4L, LocalDateTime.of(2022, 1, 1, 6, 0), Duration.of(2, ChronoUnit.HOURS));
        assertEquals(4L, timetable.getBusStop().getId());
        assertThat(timetable.getTimetableEntries())
                .hasSize(1)
                .first().satisfies(busStopScheduleEntryBO -> {
                    assertThat(busStopScheduleEntryBO)
                            .extracting(BusStopTimetableEntryBO::getScheduleBO)
                            .extracting(
                                    ScheduleBO::getId,
                                    ScheduleBO::getStartTime,
                                    schedule -> schedule.getFinalDestination().getName(),
                                    schedule -> schedule.getLine().getName()
                            )
                            .containsExactly(1L, LocalTime.of(6, 0), "Camp Street", "L1");
                    assertThat(busStopScheduleEntryBO)
                            .extracting(BusStopTimetableEntryBO::getArrival)
                            .isEqualTo(LocalDateTime.of(2022, 1, 1, 6, 1));
                });
    }

    @Test
    @Sql("/getRelevantSchedulesData.sql")
    void testGetBusStopTimetableReverseDirection() throws BusStopNotFoundException {
        long busStopId = 5L;
        BusStopTimetableBO timetable = this.objectUnderTest.getTimetable(busStopId, LocalDateTime.of(2022, 1, 1, 10, 0), Duration.of(2, ChronoUnit.HOURS));
        assertEquals(busStopId, timetable.getBusStop().getId());
        assertThat(timetable.getTimetableEntries())
                .hasSize(1)
                .first().satisfies(busStopScheduleEntryBO -> {
                    assertThat(busStopScheduleEntryBO)
                            .extracting(BusStopTimetableEntryBO::getScheduleBO)
                            .extracting(
                                    ScheduleBO::getId,
                                    ScheduleBO::getStartTime,
                                    schedule -> schedule.getFinalDestination().getName(),
                                    schedule -> schedule.getLine().getName()
                            )
                            .containsExactly(2L, LocalTime.of(11, 0), "Abbey Road", "L2");
                    assertThat(busStopScheduleEntryBO)
                            .extracting(BusStopTimetableEntryBO::getArrival)
                            .isEqualTo(LocalDateTime.of(2022, 1, 1, 11, 2));
                });
    }

    @Test
    @Transactional
    @Sql("/schedulesOverMidnight.sql")
    void testGetBusStopTimetableDefaultDirectionCrossingMidnight() throws BusStopNotFoundException {
        BusStopTimetableBO timetable = this.objectUnderTest.getTimetable(4L, LocalDateTime.of(2022, 1, 2, 2, 0), Duration.of(2, ChronoUnit.HOURS));
        assertEquals(4L, timetable.getBusStop().getId());
        assertThat(timetable.getTimetableEntries())
                .hasSize(1)
                .first().satisfies(busStopScheduleEntryBO -> {
                    assertThat(busStopScheduleEntryBO)
                            .extracting(BusStopTimetableEntryBO::getScheduleBO)
                            .extracting(
                                    ScheduleBO::getId,
                                    ScheduleBO::getStartTime,
                                    schedule -> schedule.getFinalDestination().getName(),
                                    schedule -> schedule.getLine().getName()
                            )
                            .containsExactly(1L, LocalTime.of(22, 0), "Camp Street", "L1");
                    assertThat(busStopScheduleEntryBO)
                            .extracting(BusStopTimetableEntryBO::getArrival)
                            // started the day before
                            .isEqualTo(LocalDateTime.of(2022, 1, 2, 3, 0));
                });
    }

    @Test
    @Transactional
    @Sql("/schedulesOverMidnight.sql")
    void testGetBusStopTimetableReverseDirectionCrossingMidnight() throws BusStopNotFoundException {
        BusStopTimetableBO timetable = this.objectUnderTest.getTimetable(5L, LocalDateTime.of(2022, 1, 2, 4, 0), Duration.of(2, ChronoUnit.HOURS));
        assertEquals(5L, timetable.getBusStop().getId());
        assertThat(timetable.getTimetableEntries())
                .hasSize(1)
                .first().satisfies(busStopScheduleEntryBO -> {
                    assertThat(busStopScheduleEntryBO)
                            .extracting(BusStopTimetableEntryBO::getScheduleBO)
                            .extracting(
                                    ScheduleBO::getId,
                                    ScheduleBO::getStartTime,
                                    schedule -> schedule.getFinalDestination().getName(),
                                    schedule -> schedule.getLine().getName()
                            )
                            .containsExactly(2L, LocalTime.of(21, 30), "Abbey Road", "L2");
                    assertThat(busStopScheduleEntryBO)
                            .extracting(BusStopTimetableEntryBO::getArrival)
                            // started the day before
                            .isEqualTo(LocalDateTime.of(2022, 1, 2, 4, 30));
                });
    }

    @Test
    @Sql("/getRelevantSchedulesData.sql")
    void testGetBusStopTimetableOneScheduleMultipleTimesDefaultDirection() throws BusStopNotFoundException {
        BusStopTimetableBO timetable = this.objectUnderTest.getTimetable(4L, LocalDateTime.of(2022, 1, 1, 6, 0), Duration.of(3, ChronoUnit.DAYS));
        assertEquals(4L, timetable.getBusStop().getId());
        assertThat(timetable.getTimetableEntries())
                .hasSize(3)
                .allSatisfy(busStopScheduleEntryBO -> assertThat(busStopScheduleEntryBO)
                        .extracting(BusStopTimetableEntryBO::getScheduleBO)
                        .extracting(
                                ScheduleBO::getId,
                                ScheduleBO::getStartTime,
                                schedule -> schedule.getFinalDestination().getName(),
                                schedule -> schedule.getLine().getName()
                        )
                        .containsExactly(1L, LocalTime.of(6, 0), "Camp Street", "L1"));
        assertThat(timetable.getTimetableEntries())
                .map(BusStopTimetableEntryBO::getArrival)
                .containsExactly(
                        LocalDateTime.of(2022, 1, 1, 6, 1),
                        LocalDateTime.of(2022, 1, 2, 6, 1),
                        LocalDateTime.of(2022, 1, 3, 6, 1)
                );
    }

    @Test
    @Sql("/getRelevantSchedulesData.sql")
    void testGetBusStopTimetableOneScheduleMultipleTimesReverseDirection() throws BusStopNotFoundException {
        long busStopId = 5L;
        BusStopTimetableBO timetable = this.objectUnderTest.getTimetable(busStopId, LocalDateTime.of(2022, 1, 1, 10, 0), Duration.of(3, ChronoUnit.DAYS));
        assertEquals(busStopId, timetable.getBusStop().getId());
        assertThat(timetable.getTimetableEntries())
                .hasSize(3)
                .allSatisfy(busStopScheduleEntryBO -> assertThat(busStopScheduleEntryBO)
                        .extracting(BusStopTimetableEntryBO::getScheduleBO)
                        .extracting(
                                ScheduleBO::getId,
                                ScheduleBO::getStartTime,
                                schedule -> schedule.getFinalDestination().getName(),
                                schedule -> schedule.getLine().getName()
                        )
                        .containsExactly(2L, LocalTime.of(11, 0), "Abbey Road", "L2"));
        assertThat(timetable.getTimetableEntries())
                .map(BusStopTimetableEntryBO::getArrival)
                .containsExactly(
                        LocalDateTime.of(2022, 1, 1, 11, 2),
                        LocalDateTime.of(2022, 1, 2, 11, 2),
                        LocalDateTime.of(2022, 1, 3, 11, 2)
                );

    }

    @Test
    @Sql("/getRelevantSchedulesData.sql")
    void testGetBusStopTimetableBothDirections() throws BusStopNotFoundException {
        BusStopTimetableBO timetable = this.objectUnderTest.getTimetable(1L, LocalDateTime.of(2022, 1, 1, 6, 0), Duration.of(6, ChronoUnit.HOURS));
        assertEquals(1L, timetable.getBusStop().getId());
        assertThat(timetable.getTimetableEntries()).hasSize(2);
        ArrayList<BusStopTimetableEntryBO> timetableEntries = new ArrayList<>(timetable.getTimetableEntries());

        // first entry: line 1 / default direction
        BusStopTimetableEntryBO firstEntry = timetableEntries.get(0);
        assertThat(firstEntry)
                .extracting(BusStopTimetableEntryBO::getScheduleBO)
                .extracting(
                        ScheduleBO::getId,
                        ScheduleBO::getStartTime,
                        schedule -> schedule.getFinalDestination().getName(),
                        schedule -> schedule.getLine().getName()
                )
                .containsExactly(1L, LocalTime.of(6, 0), "Camp Street", "L1");
        assertThat(firstEntry)
                .extracting(BusStopTimetableEntryBO::getArrival)
                .isEqualTo(LocalDateTime.of(2022, 1, 1, 6, 0));

        // second entry: line 2 / reverse direction
        BusStopTimetableEntryBO secondEntry = timetableEntries.get(1);
        assertThat(secondEntry)
                .extracting(BusStopTimetableEntryBO::getScheduleBO)
                .extracting(
                        ScheduleBO::getId,
                        ScheduleBO::getStartTime,
                        schedule -> schedule.getFinalDestination().getName(),
                        schedule -> schedule.getLine().getName()
                )
                .containsExactly(2L, LocalTime.of(11, 0), "Abbey Road", "L2");
        assertThat(secondEntry)
                .extracting(BusStopTimetableEntryBO::getArrival)
                .isEqualTo(LocalDateTime.of(2022, 1, 1, 11, 3));
    }

    @Test
    @Sql("/getRelevantSchedulesData.sql")
    void testGetBusStopTimetableBothDirectionsBetweenStops() throws BusStopNotFoundException {
        BusStopTimetableBO timetable = this.objectUnderTest.getTimetable(1L, LocalDateTime.of(2022, 1, 1, 10, 0), Duration.of(1, ChronoUnit.DAYS));
        assertEquals(1L, timetable.getBusStop().getId());
        assertThat(timetable.getTimetableEntries()).hasSize(2);
        ArrayList<BusStopTimetableEntryBO> timetableEntries = new ArrayList<>(timetable.getTimetableEntries());

        // first entry: line 2 / reverse direction
        BusStopTimetableEntryBO secondEntry = timetableEntries.get(0);
        assertThat(secondEntry)
                .extracting(BusStopTimetableEntryBO::getScheduleBO)
                .extracting(
                        ScheduleBO::getId,
                        ScheduleBO::getStartTime,
                        schedule -> schedule.getFinalDestination().getName(),
                        schedule -> schedule.getLine().getName()
                )
                .containsExactly(2L, LocalTime.of(11, 0), "Abbey Road", "L2");
        assertThat(secondEntry)
                .extracting(BusStopTimetableEntryBO::getArrival)
                .isEqualTo(LocalDateTime.of(2022, 1, 1, 11, 3));

        // second entry: line 1 / default direction (next day)
        BusStopTimetableEntryBO firstEntry = timetableEntries.get(1);
        assertThat(firstEntry)
                .extracting(BusStopTimetableEntryBO::getScheduleBO)
                .extracting(
                        ScheduleBO::getId,
                        ScheduleBO::getStartTime,
                        schedule -> schedule.getFinalDestination().getName(),
                        schedule -> schedule.getLine().getName()
                )
                .containsExactly(1L, LocalTime.of(6, 0), "Camp Street", "L1");
        assertThat(firstEntry)
                .extracting(BusStopTimetableEntryBO::getArrival)
                .isEqualTo(LocalDateTime.of(2022, 1, 2, 6, 0));

    }

    @Test
    @Sql("/data-test.sql")
    void testGetSchedulesForLineBusStopNonExistent() {
        assertThrows(BusStopNotFoundException.class, () -> this.objectUnderTest.getSchedulesForLineAtBusStop(7777L, 1L));
    }

    @Test
    @Sql("/data-test.sql")
    void testGetSchedulesForLineLineNonExistent() {
        assertThrows(LineNotFoundException.class, () -> this.objectUnderTest.getSchedulesForLineAtBusStop(1L, 7777L));
    }

    @Test
    @Sql("/getSchedulesForLineData.sql")
    void testGetSchedulesForLine() throws BusStopNotFoundException, LineNotFoundException {
        BusStopSchedulesBO schedulesForLineAtBusStop = this.objectUnderTest.getSchedulesForLineAtBusStop(2L, 1L);

        assertEquals(2L, schedulesForLineAtBusStop.getBusStop().getId());
        assertThat(schedulesForLineAtBusStop.getScheduleEntries()).hasSize(2);
        ArrayList<BusStopScheduleEntryBO> scheduleEntries = new ArrayList<>(schedulesForLineAtBusStop.getScheduleEntries());

        // first entry: schedule 1 / default direction
        BusStopScheduleEntryBO firstEntry = scheduleEntries.get(0);
        assertThat(firstEntry)
                .extracting(BusStopScheduleEntryBO::getSchedule)
                .extracting(
                        ScheduleBO::getId,
                        ScheduleBO::getStartTime,
                        schedule -> schedule.getFinalDestination().getName(),
                        schedule -> schedule.getLine().getName()
                )
                .containsExactly(1L, LocalTime.of(6, 0), "Camp Street", "L1");
        assertThat(firstEntry)
                .extracting(BusStopScheduleEntryBO::getArrival)
                .isEqualTo(LocalTime.of(6, 1));


        // second entry: schedule 2 / reverse direction
        BusStopScheduleEntryBO secondEntry = scheduleEntries.get(1);
        assertThat(secondEntry)
                .extracting(BusStopScheduleEntryBO::getSchedule)
                .extracting(
                        ScheduleBO::getId,
                        ScheduleBO::getStartTime,
                        schedule -> schedule.getFinalDestination().getName(),
                        schedule -> schedule.getLine().getName()
                )
                .containsExactly(2L, LocalTime.of(11, 0), "Abbey Road", "L1");
        assertThat(secondEntry)
                .extracting(BusStopScheduleEntryBO::getArrival)
                .isEqualTo(LocalTime.of( 11, 2));

    }


}