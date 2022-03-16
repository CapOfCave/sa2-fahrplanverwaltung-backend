package de.hswhameln.timetablemanager.services;

import de.hswhameln.timetablemanager.businessobjects.ScheduleBO;
import de.hswhameln.timetablemanager.entities.Schedule;
import de.hswhameln.timetablemanager.exceptions.LineNotFoundException;
import de.hswhameln.timetablemanager.exceptions.NotFoundException;
import de.hswhameln.timetablemanager.exceptions.ScheduleNotFoundException;
import de.hswhameln.timetablemanager.repositories.ScheduleRepository;
import de.hswhameln.timetablemanager.test.SpringAssistedUnitTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalTime;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ScheduleServiceTest extends SpringAssistedUnitTest {

    private final ScheduleService objectUnderTest;
    private final ScheduleRepository scheduleRepository;

    @Autowired
    ScheduleServiceTest(ScheduleService objectUnderTest, ScheduleRepository scheduleRepository) {
        this.objectUnderTest = objectUnderTest;
        this.scheduleRepository = scheduleRepository;
    }

    @Test
    @Sql("/data-test.sql")
    void testCreateSchedule() throws NotFoundException {
        ScheduleBO schedule = this.objectUnderTest.createSchedule(1L, LocalTime.of(12, 40), false);
        assertThat(schedule)
                .extracting(scheduleBO -> scheduleBO.getLine().getId(), ScheduleBO::getStartTime, ScheduleBO::isReverseDirection, ScheduleBO::getId, scheduleBO1 -> scheduleBO1.getFinalDestination().getId())
                .containsExactly(1L, LocalTime.of(12, 40), false, 2L, 5L);

        assertThat(scheduleRepository.count()).isEqualTo(2L);
    }

    @Test
    @Sql("/data-test.sql")
    void testCreateScheduleNonExistentLine() {
        assertThrows(LineNotFoundException.class, () -> this.objectUnderTest.createSchedule(7777L, LocalTime.of(12, 40), false));
    }

    @ParameterizedTest
    @Sql("/line-with-few-stops.sql")
    @ValueSource(booleans = {true, false})
    void testCreateScheduleLineWithoutStops(boolean reverseDirection) throws NotFoundException {
        ScheduleBO schedule = this.objectUnderTest.createSchedule(1L, LocalTime.of(12, 40), reverseDirection);
        assertThat(schedule)
                .extracting(scheduleBO -> scheduleBO.getLine().getId(), ScheduleBO::getStartTime, ScheduleBO::isReverseDirection, ScheduleBO::getId, ScheduleBO::getFinalDestination)
                .containsExactly(1L, LocalTime.of(12, 40), reverseDirection, 1L, null);
    }

    @ParameterizedTest
    @Sql("/line-with-few-stops.sql")
    @ValueSource(booleans = {true, false})
    void testCreateScheduleLineWithOneStop(boolean reverseDirection) throws NotFoundException {
        ScheduleBO schedule = this.objectUnderTest.createSchedule(2L, LocalTime.of(12, 40), reverseDirection);
        assertThat(schedule)
                .extracting(scheduleBO -> scheduleBO.getLine().getId(), ScheduleBO::getStartTime, ScheduleBO::isReverseDirection, ScheduleBO::getId, scheduleBO1 -> scheduleBO1.getFinalDestination().getId())
                .containsExactly(2L, LocalTime.of(12, 40), reverseDirection, 1L, 1L);
    }

    @Test
    @Sql("/data-test.sql")
    void testGetSchedules() {
        Collection<ScheduleBO> schedules = this.objectUnderTest.getSchedules();
        assertThat(schedules)
                .hasSize(1)
                .first()
                .extracting(scheduleBO -> scheduleBO.getLine().getId(), ScheduleBO::getStartTime, ScheduleBO::isReverseDirection, ScheduleBO::getId, scheduleBO1 -> scheduleBO1.getFinalDestination().getId())
                .containsExactly(1L, LocalTime.of(14, 35), false, 1L, 5L);
    }

    @Test
    @Sql("/data-test.sql")
    void testModifyScheduleNonExistentSchedule() {
        assertThrows(ScheduleNotFoundException.class, () ->  this.objectUnderTest.modifySchedule(7777L, null, null));
    }

    @Test
    @Sql("/data-test.sql")
    void testModifyNothing() throws ScheduleNotFoundException {
        ScheduleBO schedule = this.objectUnderTest.modifySchedule(1L, null, null);
        assertThat(schedule)
                .extracting(scheduleBO -> scheduleBO.getLine().getId(), ScheduleBO::getStartTime, ScheduleBO::isReverseDirection, ScheduleBO::getId, scheduleBO1 -> scheduleBO1.getFinalDestination().getId())
                .containsExactly(1L, LocalTime.of(14, 35), false, 1L, 5L);
    }

    @Test
    @Sql("/data-test.sql")
    void testModifyStartTime() throws ScheduleNotFoundException {
        ScheduleBO schedule = this.objectUnderTest.modifySchedule(1L, LocalTime.of(12, 22), null);
        assertThat(schedule)
                .extracting(scheduleBO -> scheduleBO.getLine().getId(), ScheduleBO::getStartTime, ScheduleBO::isReverseDirection, ScheduleBO::getId, scheduleBO1 -> scheduleBO1.getFinalDestination().getId())
                .containsExactly(1L, LocalTime.of(12, 22), false, 1L, 5L);

        assertThat(this.scheduleRepository.findById(1L))
                .isPresent()
                .get()
                .extracting(scheduleBO -> scheduleBO.getLine().getId(), Schedule::getStartTime, Schedule::isReverseDirection, Schedule::getId)
                .containsExactly(1L, LocalTime.of(12, 22), false, 1L);
    }

    @Test
    @Sql("/data-test.sql")
    void testModifyDirection() throws ScheduleNotFoundException {
        ScheduleBO schedule = this.objectUnderTest.modifySchedule(1L, null, true);
        assertThat(schedule)
                .extracting(scheduleBO -> scheduleBO.getLine().getId(), ScheduleBO::getStartTime, ScheduleBO::isReverseDirection, ScheduleBO::getId, scheduleBO1 -> scheduleBO1.getFinalDestination().getId())
                .containsExactly(1L, LocalTime.of(14, 35), true, 1L, 1L);

        assertThat(this.scheduleRepository.findById(1L))
                .isPresent()
                .get()
                .extracting(scheduleBO -> scheduleBO.getLine().getId(), Schedule::getStartTime, Schedule::isReverseDirection, Schedule::getId)
                .containsExactly(1L, LocalTime.of(14, 35), true, 1L);
    }

    @Test
    @Sql("/data-test.sql")
    void testModifyStartTimeAndDirection() throws ScheduleNotFoundException {
        ScheduleBO schedule = this.objectUnderTest.modifySchedule(1L, LocalTime.of(12, 22), true);
        assertThat(schedule)
                .extracting(scheduleBO -> scheduleBO.getLine().getId(), ScheduleBO::getStartTime, ScheduleBO::isReverseDirection, ScheduleBO::getId, scheduleBO1 -> scheduleBO1.getFinalDestination().getId())
                .containsExactly(1L, LocalTime.of(12, 22), true, 1L, 1L);

        assertThat(this.scheduleRepository.findById(1L))
                .isPresent()
                .get()
                .extracting(scheduleBO -> scheduleBO.getLine().getId(), Schedule::getStartTime, Schedule::isReverseDirection, Schedule::getId)
                .containsExactly(1L, LocalTime.of(12, 22), true, 1L);

    }

    @Test
    @Sql("/data-without-schedules.sql")
    void testGetSchedulesEmpty() {
        Collection<ScheduleBO> schedules = this.objectUnderTest.getSchedules();
        assertThat(schedules).isEmpty();
    }

    @Test
    @Sql("/data-test.sql")
    void testDeleteScheduleNonExistentSchedule() {
        assertThrows(ScheduleNotFoundException.class, () ->  this.objectUnderTest.deleteSchedule(7777L));
    }

    @Test
    @Sql("/data-test.sql")
    void testDeleteSchedule() throws ScheduleNotFoundException {
        this.objectUnderTest.deleteSchedule(1L);
        assertThat(this.scheduleRepository.count()).isEqualTo(0L);
    }
}