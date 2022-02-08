package de.hswhameln.timetablemanager.controller;

import de.hswhameln.timetablemanager.dto.ModifyBusStopRequest;
import de.hswhameln.timetablemanager.dto.CreateBusStopRequest;
import de.hswhameln.timetablemanager.entities.BusStop;
import de.hswhameln.timetablemanager.services.BusStopService;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@RestController
@RequestMapping("/busstops")
public class BusStopController {

    private final BusStopService busStopService;

    @Autowired
    public BusStopController(BusStopService busStopService) {
        this.busStopService = busStopService;
    }

    @GetMapping("/")
    public Collection<BusStop> getBusStops() {
        return this.busStopService.getBusStops();
    }

    @GetMapping("/{id}")
    public ResponseEntity<BusStop> getBusStop(@PathVariable long id) {
        return this.busStopService.getBusStop(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/")
    public BusStop createBusStop(@RequestBody CreateBusStopRequest createBusStopRequest) {
        return this.busStopService.createBusStop(createBusStopRequest.getName());
    }

    @PutMapping("/{id}")
    public BusStop modifyBusStop(@PathVariable long id, @RequestBody ModifyBusStopRequest modifyBusStopRequest) {
        return this.busStopService.modifyBusStop(id, modifyBusStopRequest.getName());
    }

    @DeleteMapping("/{id}")
    public void createBusStop(@PathVariable long id) {
        this.busStopService.deleteBusStop(id);
    }
}
