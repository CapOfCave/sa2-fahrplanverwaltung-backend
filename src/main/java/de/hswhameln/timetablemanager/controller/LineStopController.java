package de.hswhameln.timetablemanager.controller;

import de.hswhameln.timetablemanager.dto.requests.AddBusStopRequest;
import de.hswhameln.timetablemanager.dto.requests.ModifyLineStopRequest;
import de.hswhameln.timetablemanager.dto.responses.LineDetailDto;
import de.hswhameln.timetablemanager.dto.responses.LineStopOverviewDto;
import de.hswhameln.timetablemanager.exceptions.InvalidArgumentException;
import de.hswhameln.timetablemanager.exceptions.NotFoundException;
import de.hswhameln.timetablemanager.mapping.LineStopToDtoMapper;
import de.hswhameln.timetablemanager.mapping.LineToDtoMapper;
import de.hswhameln.timetablemanager.services.LineService;
import de.hswhameln.timetablemanager.services.LineStopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@RestController
@RequestMapping("/lines/{lineId}/busstops")
public class LineStopController {

    private final LineStopService lineStopService;
    private final LineService lineService;
    private final LineStopToDtoMapper lineStopToDtoMapper;
    private final LineToDtoMapper lineToDtoMapper;

    @Autowired
    public LineStopController(LineStopService lineStopService, LineService lineService, LineStopToDtoMapper lineStopToDtoMapper, LineToDtoMapper lineToDtoMapper) {
        this.lineStopService = lineStopService;
        this.lineService = lineService;
        this.lineStopToDtoMapper = lineStopToDtoMapper;
        this.lineToDtoMapper = lineToDtoMapper;
    }

    @GetMapping
    public Collection<LineStopOverviewDto> getBusStops(@PathVariable long lineId) throws NotFoundException {
        return this.lineStopService.getBusStops(lineId)
                .stream()
                .map(this.lineStopToDtoMapper::mapToLineStopOverviewDto)
                .toList();
    }

    @PostMapping
    public void addBusStop(@PathVariable long lineId, @RequestBody AddBusStopRequest addBusStopRequest) throws NotFoundException, InvalidArgumentException {
        this.lineStopService.addBusStop(lineId, addBusStopRequest.getBusStopId(), addBusStopRequest.getSecondsToNextStop(), addBusStopRequest.getTargetIndex());
    }

    @DeleteMapping("/{lineStopId}")
    public void removeBusStop(@PathVariable long lineId, @PathVariable long lineStopId) throws NotFoundException {
        this.lineStopService.removeBusStop(lineId, lineStopId);
    }

    @PatchMapping("/{lineStopId}")
    public LineDetailDto modifyLineStop(@PathVariable long lineId, @PathVariable long lineStopId, @RequestBody ModifyLineStopRequest modifyLineStopRequest) throws NotFoundException, InvalidArgumentException {
        this.lineStopService.modifyLineStop(lineId, lineStopId, modifyLineStopRequest.getTargetIndex(), modifyLineStopRequest.getSecondsToNextStop());

        // ignore the result as the LineStop has no meaning to this frontend. Instead, return the current Line.
        return this.lineToDtoMapper.mapToLineDetailDto(this.lineService.getLine(lineId));
    }
}
