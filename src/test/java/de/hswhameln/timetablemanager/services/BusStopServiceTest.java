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
        String name = "5th Avenue";
        this.objectUnderTest.createBusStop(name);

        assertEquals(2, busStopRepository.count());
    }

    @Test
    void testGetBusStops() {
        Collection<BusStop> busStops = this.objectUnderTest.getBusStops();
        assertThat(busStops)
                .hasSize(1)
                .first()
                .extracting(BusStop::getName)
                .isEqualTo("Abbey Road");
    }

    @Test
    void testDeleteBusStop() {
        BusStop stop = this.busStopRepository.save(new BusStop("James Street"));
        assertEquals(2, this.busStopRepository.count());
        this.objectUnderTest.deleteBusStop(stop.getId());
        assertEquals(1, this.busStopRepository.count());
    }

    @Test
    void testModifyBusStop() {
        this.objectUnderTest.modifyBusStop(1L, "Rainy Road");
        BusStop newBusStop = this.busStopRepository.findById(1L).orElseThrow();
        assertEquals("Rainy Road", newBusStop.getName());
    }
}