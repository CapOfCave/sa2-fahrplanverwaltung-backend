package de.hswhameln.timetablemanager.services;

import de.hswhameln.timetablemanager.entities.BusStop;
import de.hswhameln.timetablemanager.repositories.BusStopRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
public class BusStopService {

    private final BusStopRepository busStopRepository;

    @Autowired
    public BusStopService(BusStopRepository busStopRepository) {
        this.busStopRepository = busStopRepository;
    }

    public BusStop createBusStop(String name) {
        // TODO check if name is unique
        var busStop = new BusStop(name);
        return this.busStopRepository.save(busStop);
    }

    public Collection<BusStop> getBusStops() {
        return new ArrayList<>(this.busStopRepository.findAll());
    }

    public void deleteBusStop(long id) {
        this.busStopRepository.deleteById(id);
    }

    public BusStop modifyBusStop(long id, String name) {
        BusStop busStop = this.busStopRepository.findById(id).orElseThrow();
        busStop.setName(name);
        return this.busStopRepository.save(busStop);
    }

    public Optional<BusStop> getBusStop(long id) {
        return this.busStopRepository.findById(id);
    }
}
