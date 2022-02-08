package de.hswhameln.timetablemanager.controller;

import de.hswhameln.timetablemanager.dto.ModifyBusStopRequest;
import de.hswhameln.timetablemanager.dto.CreateBusStopRequest;
import de.hswhameln.timetablemanager.entities.BusStop;
import de.hswhameln.timetablemanager.services.BusStopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
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

    @PostMapping("/")
    public BusStop createBusStop(@RequestBody CreateBusStopRequest createBusStopRequest) {
        return this.busStopService.createBusStop(createBusStopRequest.getName());
    }

    @PutMapping("/:id")
    public BusStop modifyBusStop(@RequestParam long id, @RequestBody ModifyBusStopRequest modifyBusStopRequest) {
        return this.busStopService.modifyBusStop(id, modifyBusStopRequest.getName());
    }

    @DeleteMapping("/:id")
    public void createBusStop(@RequestParam long id) {
        this.busStopService.deleteBusStop(id);
    }
}
