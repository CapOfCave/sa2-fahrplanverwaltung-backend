package de.hswhameln.timetablemanager.controller;

import de.hswhameln.timetablemanager.dto.requests.CreateScheduleRequest;
import de.hswhameln.timetablemanager.dto.responses.BusStopDetailDto;
import de.hswhameln.timetablemanager.dto.responses.BusStopOverviewDto;
import de.hswhameln.timetablemanager.dto.responses.ScheduleOverviewDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.Collections;

@RestController
@RequestMapping("/schedule")
public class ScheduleController {


    @GetMapping
    public Collection<ScheduleOverviewDto> getSchedules() {
        return Collections.emptyList();
    }

    @PostMapping
    public ScheduleOverviewDto createSchedule(@RequestBody CreateScheduleRequest createScheduleRequest) {
        return null;
    }

    // could possibly be removed
    @GetMapping("/{scheduleId}")
    public ResponseEntity<ScheduleOverviewDto> getSchedule(@PathVariable long scheduleId) {
        return null;
    }

    @DeleteMapping("/{scheduleId}")
    public void deleteSchedule(@PathVariable long scheduleId) {

    }
}
