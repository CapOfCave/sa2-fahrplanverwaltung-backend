package de.hswhameln.timetablemanager.services;

import de.hswhameln.timetablemanager.businessobjects.BusStopScheduleBO;
import de.hswhameln.timetablemanager.businessobjects.BusStopScheduleEntryBO;
import de.hswhameln.timetablemanager.businessobjects.ScheduleBO;
import de.hswhameln.timetablemanager.entities.BusStop;
import de.hswhameln.timetablemanager.entities.Line;
import de.hswhameln.timetablemanager.entities.LineStop;
import de.hswhameln.timetablemanager.entities.Schedule;
import de.hswhameln.timetablemanager.mapping.ScheduleToBoMapper;
import de.hswhameln.timetablemanager.repositories.BusStopRepository;
import de.hswhameln.timetablemanager.repositories.ScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class BusStopService {

    private final BusStopRepository busStopRepository;

    private final ScheduleRepository scheduleRepository;

    private final ScheduleToBoMapper scheduleToBoMapper;

    @Autowired
    public BusStopService(BusStopRepository busStopRepository, ScheduleRepository scheduleRepository, ScheduleToBoMapper scheduleToBoMapper) {
        this.busStopRepository = busStopRepository;
        this.scheduleRepository = scheduleRepository;
        this.scheduleToBoMapper = scheduleToBoMapper;
    }

    public BusStop createBusStop(String name) {
        // TODO check if name is unique
        var busStop = new BusStop(name);
        return this.busStopRepository.save(busStop);
    }

    public Collection<BusStop> getBusStops() {
        return new ArrayList<>(this.busStopRepository.findAll());
    }

    public void deleteBusStop(long id) {
        this.busStopRepository.deleteById(id);
    }

    public BusStop modifyBusStop(long id, String name) {
        BusStop busStop = this.busStopRepository.findById(id).orElseThrow();
        busStop.setName(name);
        return this.busStopRepository.save(busStop);
    }

    public Optional<BusStop> getBusStop(long id) {
        return this.busStopRepository.findById(id);
    }

    @Transactional
    public BusStopScheduleBO getBusStopSchedule(long id) {
        BusStop busStop = this.getBusStop(id).orElseThrow();

        List<BusStopScheduleEntryBO> scheduleEntries = getRelevantSchedules(busStop)
                .stream()
                .map(schedule -> {
                    ScheduleBO scheduleBO = this.scheduleToBoMapper.enrichWithTargetDestination(schedule);
                    return getArrivalTimesBySchedule(id, schedule)
                            .stream()
                            .map(arrivalTime -> new BusStopScheduleEntryBO(scheduleBO, arrivalTime))
                            .toList();
                })
                .flatMap(Collection::stream)
                .toList();

        return new BusStopScheduleBO(busStop, scheduleEntries);
    }

    /**
     * Create all schedule entries caused by this schedule. Note that this may return multiple resulting values if the line crosses this bus stop multiple times.
     */
    private Collection<LocalTime> getArrivalTimesBySchedule(long busStopId, Schedule schedule) {
        return schedule.isReverseDirection() ?
                getArrivalTimesByScheduleReverseDirection(busStopId, schedule) :
                getArrivalTimesByScheduleDefaultDirection(busStopId, schedule);
    }

    private Collection<LocalTime> getArrivalTimesByScheduleDefaultDirection(long busStopId, Schedule schedule) {
        Collection<LocalTime> arrivals = new ArrayList<>();
        int secondsSinceStart = 0;
        for (LineStop lineStop : schedule.getLine().getLineStops()) {
            if (lineStop.getBusStop().getId() == busStopId) {
                LocalTime arrival = schedule.getStartTime().plus(secondsSinceStart, ChronoUnit.SECONDS);
                arrivals.add(arrival);
            }
            Integer secondsToNextStop = lineStop.getSecondsToNextStop();
            if (secondsToNextStop != null) {
                secondsSinceStart += secondsToNextStop;
            }
        }
        return arrivals;
    }

    private Collection<LocalTime> getArrivalTimesByScheduleReverseDirection(long busStopId, Schedule schedule) {
        Collection<LocalTime> arrivals = new ArrayList<>();

        int secondsSinceStart = 0;
        List<LineStop> lineStops = schedule.getLine().getLineStops();
        for (int i = lineStops.size() - 1; i >= 0; i--) {
            LineStop lineStop = lineStops.get(i);

            Integer secondsToNextStop = lineStop.getSecondsToNextStop();
            if (secondsToNextStop != null) {
                secondsSinceStart += secondsToNextStop;
            }

            if (lineStop.getBusStop().getId() == busStopId) {
                LocalTime arrival = schedule.getStartTime().plus(secondsSinceStart, ChronoUnit.SECONDS);
                arrivals.add(arrival);
            }

        }
        return arrivals;
    }

    /**
     * Return all distinct schedules that this relate to this bus stop.
     * @param busStop
     * @return
     */
    private List<Schedule> getRelevantSchedules(BusStop busStop) {
        List<Long> lineIds = busStop.getLineStops()
                .stream()
                .map(LineStop::getLine)
                .map(Line::getId)
                .distinct()
                .toList();

        return this.scheduleRepository.findByLineIdIn(lineIds);
    }
}
