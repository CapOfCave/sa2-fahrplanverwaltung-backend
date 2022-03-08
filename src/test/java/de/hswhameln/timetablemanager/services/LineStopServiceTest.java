package de.hswhameln.timetablemanager.services;

import de.hswhameln.timetablemanager.entities.BusStop;
import de.hswhameln.timetablemanager.entities.LineStop;
import de.hswhameln.timetablemanager.repositories.BusStopRepository;
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

class LineStopServiceTest extends SpringAssistedUnitTest {

    private final LineStopService lineStopService;

    private final BusStopRepository busStopRepository;

    @Autowired
    LineStopServiceTest(LineStopService lineStopService, BusStopRepository busStopRepository) {
        this.lineStopService = lineStopService;
        this.busStopRepository = busStopRepository;
    }

    @Test
    @Sql("/data-test.sql")
    void testGetBusStops() {
        Collection<LineStop> busStops = this.lineStopService.getBusStops(1L);
        assertThat(busStops).hasSize(3).map(LineStop::getIndex).containsExactly(0, 1, 2);
        assertThat(busStops).first()
                .extracting(LineStop::getIndex, LineStop::getSecondsToNextStop, lineStop -> lineStop.getLine().getId(), lineStop -> lineStop.getBusStop().getId())
                .containsExactly(0, 60, 1L, 1L);
    }

    @Test
    @Sql("/data-test.sql")
    void testGetBusStopsSortedByIndex() {
        Collection<LineStop> busStops = this.lineStopService.getBusStops(2L);
        assertThat(busStops).hasSize(3).map(LineStop::getIndex).containsExactly(0, 1, 2);
        assertThat(busStops).first()
                .extracting(LineStop::getIndex, LineStop::getSecondsToNextStop, lineStop -> lineStop.getLine().getId(), lineStop -> lineStop.getBusStop().getId())
                .containsExactly(0, 60, 2L, 1L);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2, 3})
    @Sql("/data-test.sql")
    void testAddBusStop(int targetIndex) {

        this.lineStopService.addBusStop(1L, 2L, 37, targetIndex);
        List<LineStop> busStops = this.lineStopService.getBusStops(1L);
        assertThat(busStops).hasSize(4).map(LineStop::getIndex).containsExactly(0, 1, 2, 3);
        assertThat(busStops.get(targetIndex))
                .extracting(LineStop::getSecondsToNextStop, lineStop -> lineStop.getLine().getId(), lineStop -> lineStop.getBusStop().getId())
                .containsExactly(37, 1L, 2L);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2, 3})
    @Sql("/data-test.sql")
    void testAddBusStopBusStopAlreadyExists(int targetIndex) {

        this.lineStopService.addBusStop(1L, 1L, 37, targetIndex);
        List<LineStop> busStops = this.lineStopService.getBusStops(1L);
        assertThat(busStops).hasSize(4).map(LineStop::getIndex).containsExactly(0, 1, 2, 3);
        assertThat(busStops.get(targetIndex))
                .extracting(LineStop::getSecondsToNextStop, lineStop -> lineStop.getLine().getId(), lineStop -> lineStop.getBusStop().getId())
                .containsExactly(37, 1L, 1L);
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
    }
}