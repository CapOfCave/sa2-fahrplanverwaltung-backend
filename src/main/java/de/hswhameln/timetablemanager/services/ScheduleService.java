package de.hswhameln.timetablemanager.services;

import de.hswhameln.timetablemanager.businessobjects.ScheduleBO;
import de.hswhameln.timetablemanager.entities.Line;
import de.hswhameln.timetablemanager.entities.Schedule;
import de.hswhameln.timetablemanager.exceptions.LineNotFoundException;
import de.hswhameln.timetablemanager.exceptions.NotFoundException;
import de.hswhameln.timetablemanager.exceptions.ScheduleNotFoundException;
import de.hswhameln.timetablemanager.mapping.ScheduleToBoMapper;
import de.hswhameln.timetablemanager.repositories.LineRepository;
import de.hswhameln.timetablemanager.repositories.ScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
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

    @Transactional
    public ScheduleBO createSchedule(long lineId, LocalTime startTime, boolean reverseDirection) throws NotFoundException {
        Line line = this.lineRepository.findById(lineId).orElseThrow(() -> new LineNotFoundException("lineId", lineId));
        Schedule createdSchedule = new Schedule(line, startTime, reverseDirection);
        Schedule actualSchedule = this.scheduleRepository.save(createdSchedule);
        return this.scheduleToBoMapper.enrichWithTargetDestination(actualSchedule);
    }

    @Transactional
    public ScheduleBO modifySchedule(long scheduleId, LocalTime startTime, Boolean reverseDirection) throws ScheduleNotFoundException {
        Schedule schedule = this.scheduleRepository.findById(scheduleId).orElseThrow(() -> new ScheduleNotFoundException("scheduleId", scheduleId));
        if (startTime != null) {
            schedule.setStartTime(startTime);

        }
        if (reverseDirection != null) {
            schedule.setReverseDirection(reverseDirection);
        }
        Schedule savedSchedule = this.scheduleRepository.save(schedule);
        return this.scheduleToBoMapper.enrichWithTargetDestination(savedSchedule);
    }

    @Transactional
    public Collection<ScheduleBO> getSchedules() {
        return this.scheduleRepository.findAll()
                .stream()
                .map(this.scheduleToBoMapper::enrichWithTargetDestination)
                .toList();
    }


    @Transactional
    public void deleteSchedule(long scheduleId) throws ScheduleNotFoundException {
        if (!this.scheduleRepository.existsById(scheduleId)) {
            throw new ScheduleNotFoundException("scheduleId", scheduleId);
        }
        this.scheduleRepository.deleteById(scheduleId);
    }


}
