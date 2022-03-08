package de.hswhameln.timetablemanager.services;

import de.hswhameln.timetablemanager.businessobjects.ScheduleBO;
import de.hswhameln.timetablemanager.entities.Line;
import de.hswhameln.timetablemanager.entities.Schedule;
import de.hswhameln.timetablemanager.exceptions.LineNotFoundException;
import de.hswhameln.timetablemanager.exceptions.NotFoundException;
import de.hswhameln.timetablemanager.mapping.ScheduleToBoMapper;
import de.hswhameln.timetablemanager.repositories.LineRepository;
import de.hswhameln.timetablemanager.repositories.ScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.Collection;

@Service
public class ScheduleService {

    private final LineRepository lineRepository;
    private final ScheduleRepository scheduleRepository;

    private final ScheduleToBoMapper scheduleToBoMapper;

    @Autowired
    public ScheduleService(LineRepository lineRepository, ScheduleRepository scheduleRepository, ScheduleToBoMapper scheduleToBoMapper) {
        this.lineRepository = lineRepository;
        this.scheduleRepository = scheduleRepository;
        this.scheduleToBoMapper = scheduleToBoMapper;
    }

    public ScheduleBO createSchedule(long lineId, LocalTime startTime, boolean reverseDirection) throws NotFoundException {
        Line line = this.lineRepository.findById(lineId).orElseThrow(() -> new LineNotFoundException("lineId", lineId));
        Schedule createdSchedule = new Schedule(line, startTime, reverseDirection);
        Schedule actualSchedule = this.scheduleRepository.save(createdSchedule);
        return this.scheduleToBoMapper.enrichWithTargetDestination(actualSchedule);
    }

    public Collection<ScheduleBO> getSchedules() {
        return this.scheduleRepository.findAll()
                .stream()
                .map(this.scheduleToBoMapper::enrichWithTargetDestination)
                .toList();
    }


    public void deleteSchedule(long scheduleId) {
        // TODO check integrity
        this.scheduleRepository.deleteById(scheduleId);
    }
}
