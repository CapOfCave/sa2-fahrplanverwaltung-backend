package de.hswhameln.timetablemanager.controller;

import de.hswhameln.timetablemanager.dto.AddBusStopRequest;
import de.hswhameln.timetablemanager.entities.LineStop;
import de.hswhameln.timetablemanager.exceptions.NotFoundException;
import de.hswhameln.timetablemanager.services.LineStopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collection;

@RestController
@RequestMapping("/lines/{lineId}/busstops")
public class LineStopController {

    private final LineStopService lineService;

    @Autowired
    public LineStopController(LineStopService lineService) {
        this.lineService = lineService;
    }

    @GetMapping
    public Collection<LineStop> getBusStops(@PathVariable long lineId) {
        return this.lineService.getBusStops(lineId);
    }

    @PostMapping
    public void addBusStop(@PathVariable long lineId, @RequestBody AddBusStopRequest addBusStopRequest) {
        this.lineService.addBusStop(lineId, addBusStopRequest.getBusStopId(), addBusStopRequest.getSecondsToNextStop(), addBusStopRequest.getTargetIndex());
    }

    @DeleteMapping("/{lineStopId}")
    public void removeBusStop(@PathVariable long lineId, @PathVariable long lineStopId) {
        try {
            this.lineService.removeBusStop(lineId, lineStopId);
        } catch (NotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }
}
