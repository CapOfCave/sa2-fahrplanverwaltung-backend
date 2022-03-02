package de.hswhameln.timetablemanager.controller;

import de.hswhameln.timetablemanager.businessobjects.BusStopScheduleBO;
import de.hswhameln.timetablemanager.dto.requests.CreateBusStopRequest;
import de.hswhameln.timetablemanager.dto.requests.ModifyBusStopRequest;
import de.hswhameln.timetablemanager.dto.responses.BusStopDetailDto;
import de.hswhameln.timetablemanager.dto.responses.BusStopOverviewDto;
import de.hswhameln.timetablemanager.dto.responses.BusStopScheduleDto;
import de.hswhameln.timetablemanager.entities.BusStop;
import de.hswhameln.timetablemanager.mapping.BusStopScheduleToDtoMapper;
import de.hswhameln.timetablemanager.mapping.BusStopToDtoMapper;
import de.hswhameln.timetablemanager.services.BusStopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
    public BusStopDetailDto createBusStop(@RequestBody CreateBusStopRequest createBusStopRequest) {
        BusStop busStop = this.busStopService.createBusStop(createBusStopRequest.getName());
        return this.busStopToDtoMapper.mapToBusStopDetailDto(busStop);
    }

    @PatchMapping("/{busStopId}")
    public BusStopDetailDto modifyBusStop(@PathVariable long busStopId, @RequestBody ModifyBusStopRequest modifyBusStopRequest) {
        BusStop busStop = this.busStopService.modifyBusStop(busStopId, modifyBusStopRequest.getName());
        return this.busStopToDtoMapper.mapToBusStopDetailDto(busStop);
    }

    @GetMapping("/{busStopId}")
    public ResponseEntity<BusStopDetailDto> getBusStop(@PathVariable long busStopId) {
        return this.busStopService.getBusStop(busStopId)
                .map(this.busStopToDtoMapper::mapToBusStopDetailDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{busStopId}")
    public void deleteBusStop(@PathVariable long busStopId) {
        this.busStopService.deleteBusStop(busStopId);
    }

    @GetMapping("/{busStopId}/schedule")
    public BusStopScheduleDto getBusStopSchedule(@PathVariable long busStopId) {
        BusStopScheduleBO busStopSchedule = this.busStopService.getBusStopSchedule(busStopId);
        return this.busStopScheduleToDtoMapper.mapToBusStopScheduleDto(busStopSchedule);
    }
}
