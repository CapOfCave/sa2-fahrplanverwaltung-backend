package de.hswhameln.timetablemanager.services;

import de.hswhameln.timetablemanager.businessobjects.ScheduleBO;
import de.hswhameln.timetablemanager.entities.BusStop;
import de.hswhameln.timetablemanager.entities.Line;
import de.hswhameln.timetablemanager.entities.LineStop;
import de.hswhameln.timetablemanager.entities.Schedule;
import de.hswhameln.timetablemanager.exceptions.NotFoundException;
import de.hswhameln.timetablemanager.repositories.LineRepository;
import de.hswhameln.timetablemanager.repositories.ScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
public class ScheduleService {

    private final LineRepository lineRepository;
    private final ScheduleRepository scheduleRepository;

    @Autowired
    public ScheduleService(LineRepository lineRepository, ScheduleRepository scheduleRepository) {
        this.lineRepository = lineRepository;
        this.scheduleRepository = scheduleRepository;
    }

    public ScheduleBO createSchedule(long lineId, LocalTime startTime, boolean reverseDirection) throws NotFoundException {
        Line line = this.lineRepository.findById(lineId).orElseThrow(() -> new NotFoundException("Line not found"));
        Schedule createdSchedule = new Schedule(line, startTime, reverseDirection);
        Schedule actualSchedule = this.scheduleRepository.save(createdSchedule);
        return enrichWithTargetDestination(actualSchedule);
    }

    private ScheduleBO enrichWithTargetDestination(Schedule schedule) {
        Optional<BusStop> targetDestination = getTargetDestination(schedule.getLine(), schedule.isReverseDirection());
        return new ScheduleBO(schedule.getId(), schedule.getLine(), schedule.getStartTime(), targetDestination.orElse(null));
    }

    public Collection<ScheduleBO> getSchedules() {
        return this.scheduleRepository.findAll()
                .stream()
                .map(this::enrichWithTargetDestination)
                .toList();
    }

    private Optional<BusStop> getTargetDestination(Line line, boolean reverseDirection) {
        List<LineStop> lineStops = line.getLineStops();
        if (lineStops.isEmpty()) {
            return Optional.empty();
        }
        LineStop targetDestination = reverseDirection ? lineStops.get(lineStops.size() - 1) : lineStops.get(0);
        return Optional.of(targetDestination.getBusStop());
    }


    public void deleteSchedule(long scheduleId) {
        // TODO check integrity
        this.scheduleRepository.deleteById(scheduleId);
    }
}
