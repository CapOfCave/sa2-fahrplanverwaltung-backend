package de.hswhameln.timetablemanager.controller;

import de.hswhameln.timetablemanager.businessobjects.ScheduleBO;
import de.hswhameln.timetablemanager.dto.requests.CreateScheduleRequest;
import de.hswhameln.timetablemanager.dto.requests.ModifyScheduleRequest;
import de.hswhameln.timetablemanager.dto.responses.ScheduleOverviewDto;
import de.hswhameln.timetablemanager.exceptions.NotFoundException;
import de.hswhameln.timetablemanager.mapping.ScheduleToDtoMapper;
import de.hswhameln.timetablemanager.services.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@RestController
@RequestMapping("/schedules")
public class ScheduleController {

    private final ScheduleService scheduleService;
    private final ScheduleToDtoMapper scheduleToDtoMapper;

    @Autowired
    public ScheduleController(ScheduleService scheduleService, ScheduleToDtoMapper scheduleToDtoMapper) {
        this.scheduleService = scheduleService;
        this.scheduleToDtoMapper = scheduleToDtoMapper;
    }

    @GetMapping
    public Collection<ScheduleOverviewDto> getSchedules() {
        return this.scheduleService.getSchedules()
                .stream()
                .map(this.scheduleToDtoMapper::mapToScheduleOverviewDto)
                .toList();
    }

    @PostMapping
    public ScheduleOverviewDto createSchedule(@RequestBody CreateScheduleRequest createScheduleRequest) throws NotFoundException {
        ScheduleBO schedule = this.scheduleService.createSchedule(createScheduleRequest.getLineId(), createScheduleRequest.getStartTime(), createScheduleRequest.isReverseDirection());
        return this.scheduleToDtoMapper.mapToScheduleOverviewDto(schedule);
    }

    @DeleteMapping("/{scheduleId}")
    public void deleteSchedule(@PathVariable long scheduleId) {
        this.scheduleService.deleteSchedule(scheduleId);
    }

    @PatchMapping("/{scheduleId}")
    public ScheduleOverviewDto modifySchedule(@PathVariable long scheduleId, @RequestBody ModifyScheduleRequest modifyScheduleRequest) throws NotFoundException {
        ScheduleBO schedule = this.scheduleService.modifySchedule(scheduleId, modifyScheduleRequest.getStartTime(), modifyScheduleRequest.getReverseDirection());
        return this.scheduleToDtoMapper.mapToScheduleOverviewDto(schedule);
    }
}
