package de.hswhameln.timetablemanager.controller;

import de.hswhameln.timetablemanager.dto.requests.AddBusStopRequest;
import de.hswhameln.timetablemanager.dto.responses.LineStopOverviewDto;
import de.hswhameln.timetablemanager.exceptions.BusStopNotFoundException;
import de.hswhameln.timetablemanager.exceptions.LineNotFoundException;
import de.hswhameln.timetablemanager.exceptions.NotFoundException;
import de.hswhameln.timetablemanager.mapping.LineStopToDtoMapper;
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
    private final LineStopToDtoMapper lineStopToDtoMapper;

    @Autowired
    public LineStopController(LineStopService lineService, LineStopToDtoMapper lineStopToDtoMapper) {
        this.lineService = lineService;
        this.lineStopToDtoMapper = lineStopToDtoMapper;
    }

    @GetMapping
    public Collection<LineStopOverviewDto> getBusStops(@PathVariable long lineId) throws NotFoundException {
        return this.lineService.getBusStops(lineId)
                .stream()
                .map(this.lineStopToDtoMapper::mapToLineStopOverviewDto)
                .toList();
    }

    @PostMapping
    public void addBusStop(@PathVariable long lineId, @RequestBody AddBusStopRequest addBusStopRequest) throws NotFoundException {
        this.lineService.addBusStop(lineId, addBusStopRequest.getBusStopId(), addBusStopRequest.getSecondsToNextStop(), addBusStopRequest.getTargetIndex());
    }

    @DeleteMapping("/{lineStopId}")
    public void removeBusStop(@PathVariable long lineId, @PathVariable long lineStopId) throws NotFoundException {
            this.lineService.removeBusStop(lineId, lineStopId);
    }
}
