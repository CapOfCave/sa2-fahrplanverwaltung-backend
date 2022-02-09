package de.hswhameln.timetablemanager.services;

import de.hswhameln.timetablemanager.entities.BusStop;
import de.hswhameln.timetablemanager.repositories.BusStopRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class BusStopServiceTest {

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
    void testGetBusStops() {
        int count = (int) this.busStopRepository.count();
        Collection<BusStop> busStops = this.objectUnderTest.getBusStops();
        assertThat(busStops)
                .hasSizeGreaterThan(1)
                .hasSize(count);
    }

    @Test
    void testGetBusStop() {
        BusStop busStop = this.objectUnderTest.getBusStop(1).orElseThrow();
        assertEquals("Abbey Road", busStop.getName());
    }

    @Test
    void testDeleteBusStop() {
        long targetId = 8;
        long countBefore = busStopRepository.count();
        this.objectUnderTest.deleteBusStop(targetId);
        assertEquals(countBefore - 1, this.busStopRepository.count());
        assertThat(this.busStopRepository.findById(targetId)).isEmpty();
    }

    @Test
    void testModifyBusStop() {
        this.objectUnderTest.modifyBusStop(1L, "Rainy Road");
        BusStop newBusStop = this.busStopRepository.findById(1L).orElseThrow();
        assertEquals("Rainy Road", newBusStop.getName());
    }
}