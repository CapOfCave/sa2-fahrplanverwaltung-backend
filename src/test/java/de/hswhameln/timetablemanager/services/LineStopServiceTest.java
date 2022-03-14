package de.hswhameln.timetablemanager.services;

import de.hswhameln.timetablemanager.entities.BusStop;
import de.hswhameln.timetablemanager.entities.LineStop;
import de.hswhameln.timetablemanager.exceptions.BusStopNotFoundException;
import de.hswhameln.timetablemanager.exceptions.InvalidArgumentException;
import de.hswhameln.timetablemanager.exceptions.LineNotFoundException;
import de.hswhameln.timetablemanager.exceptions.LineStopNotFoundException;
import de.hswhameln.timetablemanager.repositories.BusStopRepository;
import de.hswhameln.timetablemanager.repositories.LineStopRepository;
import de.hswhameln.timetablemanager.test.SpringAssistedUnitTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

import java.util.Collection;
import java.util.List;
import java.util.stream.LongStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LineStopServiceTest extends SpringAssistedUnitTest {

    private final LineStopService lineStopService;

    private final BusStopRepository busStopRepository;

    private final LineStopRepository lineStopRepository;

    @Autowired
    LineStopServiceTest(LineStopService lineStopService, BusStopRepository busStopRepository, LineStopRepository lineStopRepository) {
        this.lineStopService = lineStopService;
        this.busStopRepository = busStopRepository;
        this.lineStopRepository = lineStopRepository;
    }

    @Test
    @Sql("/data-test.sql")
    void testGetBusStops() throws LineNotFoundException {
        Collection<LineStop> busStops = this.lineStopService.getBusStops(1L);
        assertThat(busStops).hasSize(3).map(LineStop::getIndex).containsExactly(0, 1, 2);
        assertThat(busStops).first()
                .extracting(LineStop::getIndex, LineStop::getSecondsToNextStop, lineStop -> lineStop.getLine().getId(), lineStop -> lineStop.getBusStop().getId())
                .containsExactly(0, 60, 1L, 1L);
    }

    @Test
    @Sql("/data-test.sql")
    void testGetBusStopsSortedByIndex() throws LineNotFoundException {
        Collection<LineStop> busStops = this.lineStopService.getBusStops(2L);
        assertThat(busStops).hasSize(3).map(LineStop::getIndex).containsExactly(0, 1, 2);
        assertThat(busStops).first()
                .extracting(LineStop::getIndex, LineStop::getSecondsToNextStop, lineStop -> lineStop.getLine().getId(), lineStop -> lineStop.getBusStop().getId())
                .containsExactly(0, 60, 2L, 1L);
    }

    @Test
    @Sql("/data-test.sql")
    void testGetBusStopsNonexistentLine() {
        assertThrows(LineNotFoundException.class, () -> this.lineStopService.getBusStops(7777L));
    }

    @Test
    @Sql("/busline-with-duplicate-stop.sql")
    void testGetBusStopsWithDuplicate() throws LineNotFoundException {
        List<LineStop> busStops = this.lineStopService.getBusStops(1L);
        assertThat(busStops).hasSize(4).map(LineStop::getIndex).containsExactly(0, 1, 2, 3);
        assertThat(busStops.get(0).getBusStop()).isSameAs(busStops.get(3).getBusStop());
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2, 3})
    @Sql("/data-test.sql")
    void testAddBusStop(int targetIndex) throws BusStopNotFoundException, LineNotFoundException, InvalidArgumentException {

        this.lineStopService.addBusStop(1L, 2L, 37, targetIndex);
        List<LineStop> busStops = this.lineStopService.getBusStops(1L);
        assertThat(busStops).hasSize(4).map(LineStop::getIndex).containsExactly(0, 1, 2, 3);
        assertThat(busStops.get(targetIndex))
                .extracting(LineStop::getSecondsToNextStop, lineStop -> lineStop.getLine().getId(), lineStop -> lineStop.getBusStop().getId())
                .containsExactly(37, 1L, 2L);
    }

    @Test
    @Sql("/busline-with-duplicate-stop.sql")
    void testAddBusStopWithDuplicates() throws BusStopNotFoundException, LineNotFoundException, InvalidArgumentException {
        int targetIndex = 4;
        this.lineStopService.addBusStop(1L, 1L, 37, targetIndex);
        List<LineStop> busStops = this.lineStopService.getBusStops(1L);
        assertThat(busStops).hasSize(5).map(LineStop::getIndex).containsExactly(0, 1, 2, 3, 4);
        assertThat(busStops.get(targetIndex))
                .extracting(LineStop::getSecondsToNextStop, lineStop -> lineStop.getLine().getId(), lineStop -> lineStop.getBusStop().getId())
                .containsExactly(37, 1L, 1L);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2, 3})
    @Sql("/data-test.sql")
    void testAddBusStopBusStopAlreadyExists(int targetIndex) throws BusStopNotFoundException, LineNotFoundException, InvalidArgumentException {

        this.lineStopService.addBusStop(1L, 1L, 37, targetIndex);
        List<LineStop> busStops = this.lineStopService.getBusStops(1L);
        assertThat(busStops).hasSize(4).map(LineStop::getIndex).containsExactly(0, 1, 2, 3);
        assertThat(busStops.get(targetIndex))
                .extracting(LineStop::getSecondsToNextStop, lineStop -> lineStop.getLine().getId(), lineStop -> lineStop.getBusStop().getId())
                .containsExactly(37, 1L, 1L);
    }

    @Test
    @Sql("/data-test.sql")
    void testAddBusStopToNonexistentLine() {
        assertThrows(LineNotFoundException.class, () -> this.lineStopService.addBusStop(7777L, 1L, 1, 1));
    }

    @Test
    @Sql("/data-test.sql")
    void testAddBusStopNonexistentBusStop() {
        assertThrows(BusStopNotFoundException.class, () -> this.lineStopService.addBusStop(1L, 7777L, 1, 1));
    }

    @Test
    @Sql("/data-test.sql")
    void testAddBusStopNegativeTargetIndex() {
        assertThrows(InvalidArgumentException.class, () -> this.lineStopService.addBusStop(1L, 1L, 1, -1));
    }

    @Test
    @Sql("/data-test.sql")
    void testAddBusStopTooHighTargetIndex() {
        assertThrows(InvalidArgumentException.class, () -> this.lineStopService.addBusStop(1L, 1L, 1, 4));
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3})
    @Sql("/data-test.sql")
    void testRemoveBusStop(int lineStopIdToRemove) throws Exception {

        this.lineStopService.removeBusStop(1L, lineStopIdToRemove);
        List<LineStop> busStops = this.lineStopService.getBusStops(1L);
        assertThat(busStops).hasSize(2).map(LineStop::getIndex).containsExactly(0, 1);
        Long[] expectedLineStops = LongStream.range(1, 4).filter(i -> i != lineStopIdToRemove).boxed().toArray(Long[]::new);
        assertThat(busStops).map(LineStop::getId).containsExactly(expectedLineStops);

        // all bus stops still exist
        assertThat(this.busStopRepository.findAll()).map(BusStop::getId).contains(1L, 4L, 5L);
        // but the removed one was actually removed
        assertThat(this.lineStopRepository.findById((long) lineStopIdToRemove)).isEmpty();
    }

    @Test
    @Sql("/busline-with-duplicate-stop.sql")
    void testRemoveBusStopDuplicateBusStop() throws Exception {
        int lineStopIdToRemove = 4;
        this.lineStopService.removeBusStop(1L, lineStopIdToRemove);
        List<LineStop> busStops = this.lineStopService.getBusStops(1L);
        assertThat(busStops).hasSize(3).map(LineStop::getIndex).containsExactly(0, 1, 2);
        assertThat(busStops).map(LineStop::getId).containsExactly(1L, 2L, 3L);

        // all bus stops still exist
        assertThat(this.busStopRepository.findAll()).map(BusStop::getId).contains(1L, 4L, 5L);
    }

    @Test
    @Sql("/data-test.sql")
    void testRemoveBusStopNonexistentLineStop() {
        assertThrows(LineStopNotFoundException.class, () -> this.lineStopService.removeBusStop(1L, 7777L));
    }

    @Test
    @Sql("/data-test.sql")
    void testRemoveBusStopFromNonexistentLine() {
        assertThrows(LineNotFoundException.class, () -> this.lineStopService.removeBusStop(7777L, 1L));
    }

    @Test
    @Sql("/data-test.sql")
    void testRemoveBusStopLineStopNotOnThisLine() {
        LineStopNotFoundException lineStopNotFoundException = assertThrows(LineStopNotFoundException.class, () -> this.lineStopService.removeBusStop(2, 1L));
        assertThat(lineStopNotFoundException.getMessage()).contains("It does not exist on line 2, but on line 1.");
    }

    @Test
    @Sql("/data-test.sql")
    void testModifyLineStopSecondsToNextStop() throws LineNotFoundException, LineStopNotFoundException, InvalidArgumentException {
        LineStop lineStop = this.lineStopService.modifyLineStop(1L, 2L, null, 47);
        assertThat(lineStop.getSecondsToNextStop()).isEqualTo(47);
    }

    @Test
    @Sql("/data-test.sql")
    void testModifyLineStopNonexistentLine() {
        assertThrows(LineNotFoundException.class, () -> this.lineStopService.modifyLineStop(7777L, 2L, null, null));
    }

    @Test
    @Sql("/data-test.sql")
    void testModifyLineStopNonexistentLineStop() {
        assertThrows(LineStopNotFoundException.class, () -> this.lineStopService.modifyLineStop(1L, 7777L, null, null));
    }

    @Test
    @Sql("/data-test.sql")
    void testModifyLineStopLineStopOnWrongLine() {
        assertThrows(LineStopNotFoundException.class, () -> this.lineStopService.modifyLineStop(2L, 1L, null, null));
    }

    @Test
    @Sql("/data-test.sql")
    void testModifyLineStopMoveToSameIndex() throws LineNotFoundException, LineStopNotFoundException, InvalidArgumentException {

        LineStop returnedLineStop = this.lineStopService.modifyLineStop(2L, 6L, 1, null);
        assertThat(returnedLineStop).extracting(LineStop::getIndex, LineStop::getSecondsToNextStop).containsExactly(1, 30);

        List<LineStop> busStops = this.lineStopService.getBusStops(2L);
        assertThat(busStops).hasSize(3).map(LineStop::getIndex).containsExactly(0, 1, 2);
        assertThat(busStops.get(1))
                .extracting(LineStop::getSecondsToNextStop, lineStop -> lineStop.getLine().getId(), lineStop -> lineStop.getBusStop().getId())
                .containsExactly(30, 2L, 2L);
    }

    @Test
    @Sql("/data-test-linestops.sql")
    void testModifyLineStopMoveToBack() throws LineNotFoundException, LineStopNotFoundException, InvalidArgumentException {

        var lineId = 1L;
        var lineStopId = 2L;
        var targetIndex = 2;
        Integer expectedSecondsToNextStop = 120;
        LineStop returnedLineStop = this.lineStopService.modifyLineStop(lineId, lineStopId, targetIndex, null);
        assertThat(returnedLineStop).extracting(LineStop::getIndex, LineStop::getSecondsToNextStop).containsExactly(targetIndex, expectedSecondsToNextStop);

        List<LineStop> busStops = this.lineStopService.getBusStops(lineId);
        assertThat(busStops).hasSize(4).map(LineStop::getIndex).containsExactly(0, 1, 2, 3);
        assertThat(busStops).map(LineStop::getBusStop).map(BusStop::getId).containsExactly(1L, 3L, 2L, 4L);

        assertThat(busStops.get(targetIndex))
                .extracting(LineStop::getSecondsToNextStop, lineStop -> lineStop.getLine().getId(), lineStop -> lineStop.getBusStop().getId())
                .containsExactly(expectedSecondsToNextStop, lineId, 2L);
    }

    @Test
    @Sql("/data-test-linestops.sql")
    void testModifyLineStopMoveToFront() throws LineNotFoundException, LineStopNotFoundException, InvalidArgumentException {

        var lineId = 1L;
        var lineStopId = 3L;
        var targetIndex = 1;
        Integer expectedSecondsToNextStop = 240;
        LineStop returnedLineStop = this.lineStopService.modifyLineStop(lineId, lineStopId, targetIndex, null);
        assertThat(returnedLineStop).extracting(LineStop::getIndex, LineStop::getSecondsToNextStop).containsExactly(targetIndex, expectedSecondsToNextStop);

        List<LineStop> busStops = this.lineStopService.getBusStops(lineId);
        assertThat(busStops).hasSize(4).map(LineStop::getIndex).containsExactly(0, 1, 2, 3);
        assertThat(busStops).map(LineStop::getBusStop).map(BusStop::getId).containsExactly(1L, 3L, 2L, 4L);

        assertThat(busStops.get(targetIndex))
                .extracting(LineStop::getSecondsToNextStop, lineStop -> lineStop.getLine().getId(), lineStop -> lineStop.getBusStop().getId())
                .containsExactly(expectedSecondsToNextStop, lineId, 3L);
    }


    @Test
    @Sql("/data-test-linestops.sql")
    void testModifyLineStopMoveToLast() throws LineNotFoundException, LineStopNotFoundException, InvalidArgumentException {

        var lineId = 1L;
        var lineStopId = 1L;
        var targetIndex = 3;
        Integer expectedSecondsToNextStop = 60;
        LineStop returnedLineStop = this.lineStopService.modifyLineStop(lineId, lineStopId, targetIndex, null);
        assertThat(returnedLineStop).extracting(LineStop::getIndex, LineStop::getSecondsToNextStop).containsExactly(targetIndex, expectedSecondsToNextStop);

        List<LineStop> busStops = this.lineStopService.getBusStops(lineId);
        assertThat(busStops).hasSize(4).map(LineStop::getIndex).containsExactly(0, 1, 2, 3);
        assertThat(busStops).map(LineStop::getBusStop).map(BusStop::getId).containsExactly(2L, 3L, 4L, 1L);

        assertThat(busStops.get(targetIndex))
                .extracting(LineStop::getSecondsToNextStop, lineStop -> lineStop.getLine().getId(), lineStop -> lineStop.getBusStop().getId())
                .containsExactly(expectedSecondsToNextStop, lineId, 1L);
    }

    @Test
    @Sql("/data-test-linestops.sql")
    void testModifyLineStopMoveToFirst() throws LineNotFoundException, LineStopNotFoundException, InvalidArgumentException {

        var lineId = 1L;
        var lineStopId = 4L;
        var targetIndex = 0;
        Integer expectedSecondsToNextStop = null;
        LineStop returnedLineStop = this.lineStopService.modifyLineStop(lineId, lineStopId, targetIndex, null);
        assertThat(returnedLineStop).extracting(LineStop::getIndex, LineStop::getSecondsToNextStop).containsExactly(targetIndex, expectedSecondsToNextStop);

        List<LineStop> busStops = this.lineStopService.getBusStops(lineId);
        assertThat(busStops).hasSize(4).map(LineStop::getIndex).containsExactly(0, 1, 2, 3);
        assertThat(busStops).map(LineStop::getBusStop).map(BusStop::getId).containsExactly(4L, 1L, 2L, 3L);

        assertThat(busStops.get(targetIndex))
                .extracting(LineStop::getSecondsToNextStop, lineStop -> lineStop.getLine().getId(), lineStop -> lineStop.getBusStop().getId())
                .containsExactly(expectedSecondsToNextStop, lineId, 4L);
    }

    @Test
    @Sql("/data-test-linestops.sql")
    void testModifyLineStopMoveToNegativeIndex() {
        assertThrows(InvalidArgumentException.class, () -> this.lineStopService.modifyLineStop(1L, 1L, -1, null));
    }

    @Test
    @Sql("/data-test-linestops.sql")
    void testModifyLineStopMoveToTooHighIndex() {
        assertThrows(InvalidArgumentException.class, () -> this.lineStopService.modifyLineStop(1L, 1L, 4, null));
    }

}