package de.hswhameln.timetablemanager.controller;

import de.hswhameln.timetablemanager.businessobjects.BusStopSchedulesBO;
import de.hswhameln.timetablemanager.businessobjects.BusStopTimetableBO;
import de.hswhameln.timetablemanager.dto.requests.CreateBusStopRequest;
import de.hswhameln.timetablemanager.dto.requests.ModifyBusStopRequest;
import de.hswhameln.timetablemanager.dto.responses.BusStopDetailDto;
import de.hswhameln.timetablemanager.dto.responses.BusStopOverviewDto;
import de.hswhameln.timetablemanager.dto.responses.BusStopSchedulesDto;
import de.hswhameln.timetablemanager.dto.responses.BusStopTimetableDto;
import de.hswhameln.timetablemanager.entities.BusStop;
import de.hswhameln.timetablemanager.exceptions.BusStopNotFoundException;
import de.hswhameln.timetablemanager.exceptions.DeletionForbiddenException;
import de.hswhameln.timetablemanager.exceptions.LineNotFoundException;
import de.hswhameln.timetablemanager.exceptions.NameAlreadyTakenException;
import de.hswhameln.timetablemanager.mapping.BusStopScheduleToDtoMapper;
import de.hswhameln.timetablemanager.mapping.BusStopToDtoMapper;
import de.hswhameln.timetablemanager.services.BusStopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collection;

@RestController
@RequestMapping("/busstops")
public class BusStopController {

    private final BusStopService busStopService;
    private final BusStopToDtoMapper busStopToDtoMapper;
    private final BusStopScheduleToDtoMapper busStopScheduleToDtoMapper;

    @Autowired
    public BusStopController(BusStopService busStopService, BusStopToDtoMapper busStopToDtoMapper, BusStopScheduleToDtoMapper busStopScheduleToDtoMapper) {
        this.busStopService = busStopService;
        this.busStopToDtoMapper = busStopToDtoMapper;
        this.busStopScheduleToDtoMapper = busStopScheduleToDtoMapper;
    }

    @GetMapping
    public Collection<BusStopOverviewDto> getBusStops() {
        return this.busStopService.getBusStops()
                .stream()
                .map(this.busStopToDtoMapper::mapToBusStopOverviewDto)
                .toList();
    }

    @PostMapping
    public BusStopDetailDto createBusStop(@RequestBody CreateBusStopRequest createBusStopRequest) throws NameAlreadyTakenException {
        BusStop busStop = this.busStopService.createBusStop(createBusStopRequest.getName());
        return this.busStopToDtoMapper.mapToBusStopDetailDto(busStop);
    }

    @PatchMapping("/{busStopId}")
    public BusStopDetailDto modifyBusStop(@PathVariable long busStopId, @RequestBody ModifyBusStopRequest modifyBusStopRequest) throws BusStopNotFoundException, NameAlreadyTakenException {
        BusStop busStop = this.busStopService.modifyBusStop(busStopId, modifyBusStopRequest.getName());
        return this.busStopToDtoMapper.mapToBusStopDetailDto(busStop);
    }

    @GetMapping("/{busStopId}")
    public BusStopDetailDto getBusStop(@PathVariable long busStopId) throws BusStopNotFoundException {
        BusStop busStop = this.busStopService.getBusStop(busStopId);
        return this.busStopToDtoMapper.mapToBusStopDetailDto(busStop);
    }

    @DeleteMapping("/{busStopId}")
    public void deleteBusStop(@PathVariable long busStopId) throws BusStopNotFoundException, DeletionForbiddenException {
        this.busStopService.deleteBusStop(busStopId);
    }

    @GetMapping("/{busStopId}/timetable")
    public BusStopTimetableDto getTimetable(
            @PathVariable long busStopId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam long durationSeconds
    ) throws BusStopNotFoundException {
        BusStopTimetableBO timetable = this.busStopService.getTimetable(busStopId, startTime, Duration.ofSeconds(durationSeconds));
        return this.busStopScheduleToDtoMapper.mapToBusStopTimetableDto(timetable);
    }

    @GetMapping("/{busStopId}/schedule/{lineId}")
    public BusStopSchedulesDto getSchedulesForLineAtBusStop(
            @PathVariable long busStopId,
            @PathVariable long lineId
    ) throws BusStopNotFoundException, LineNotFoundException {
        BusStopSchedulesBO schedules = this.busStopService.getSchedulesForLineAtBusStop(busStopId, lineId);
        return this.busStopScheduleToDtoMapper.mapToBusStopSchedulesDto(schedules);
    }
}
