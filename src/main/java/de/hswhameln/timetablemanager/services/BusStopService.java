package de.hswhameln.timetablemanager.services;

import de.hswhameln.timetablemanager.businessobjects.BusStopScheduleBO;
import de.hswhameln.timetablemanager.businessobjects.BusStopScheduleEntryBO;
import de.hswhameln.timetablemanager.businessobjects.ScheduleBO;
import de.hswhameln.timetablemanager.entities.BusStop;
import de.hswhameln.timetablemanager.entities.Line;
import de.hswhameln.timetablemanager.entities.LineStop;
import de.hswhameln.timetablemanager.mapping.ScheduleToBoMapper;
import de.hswhameln.timetablemanager.repositories.BusStopRepository;
import de.hswhameln.timetablemanager.repositories.ScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
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

    /**
     * Return all bus lines that stop at this bus stop combined with their arrival time
     */
    @Transactional
    public BusStopScheduleBO getBusStopSchedule(long id) {
        BusStop busStop = this.getBusStop(id).orElseThrow();

        List<BusStopScheduleEntryBO> scheduleEntries = getRelevantSchedules(busStop)
                .stream()
                .map(schedule -> getBusStopScheduleEntriesForSchedule(id, schedule))
                .flatMap(Collection::stream)
                .sorted(Comparator.comparing(BusStopScheduleEntryBO::getArrival))
                .toList();

        return new BusStopScheduleBO(busStop, scheduleEntries);
    }

    /**
     * Create all schedule entries caused by this schedule. Note that this may return multiple resulting values if the line crosses this bus stop multiple times.
     */
    private List<BusStopScheduleEntryBO> getBusStopScheduleEntriesForSchedule(long busStopId, ScheduleBO schedule) {
        return getDurationsBySchedule(busStopId, schedule)
                .stream()
                .map(duration -> new BusStopScheduleEntryBO(schedule, schedule.getStartTime().plus(duration)))
                .toList();
    }

    /**
     * For a single schedule, return the time it takes between the start of the ride and the arrival at this bus station.
     * <p>
     * This wil usually return a Collection of size 1 unless the schedule's line passes this bus stop multiple times within one schedule.
     *
     */
    private Collection<Duration> getDurationsBySchedule(long busStopId, ScheduleBO schedule) {
        return schedule.isReverseDirection() ?
                getLineStopDurationsReverseDirection(busStopId, schedule.getLine()) :
                getLineStopDurationsDefaultDirection(busStopId, schedule.getLine());
    }

    private Collection<Duration> getLineStopDurationsDefaultDirection(long busStopId, Line line) {
        Collection<Duration> durations = new ArrayList<>();
        long secondsSinceStart = 0;
        for (LineStop lineStop : line.getLineStops()) {
            if (lineStop.getBusStop().getId() == busStopId) {
                durations.add(Duration.ofSeconds(secondsSinceStart));
            }
            Integer secondsToNextStop = lineStop.getSecondsToNextStop();
            if (secondsToNextStop != null) {
                secondsSinceStart += secondsToNextStop;
            }
        }
        return durations;
    }

    private Collection<Duration> getLineStopDurationsReverseDirection(long busStopId, Line line) {
        Collection<Duration> arrivals = new ArrayList<>();

        long secondsSinceStart = 0;
        List<LineStop> lineStops = line.getLineStops();
        for (int i = lineStops.size() - 1; i >= 0; i--) {
            LineStop lineStop = lineStops.get(i);

            Integer secondsToNextStop = lineStop.getSecondsToNextStop();
            // ignore the secondsToNextStop of the last stop (= the first in reverse order) since it doesn't make sense
            if (secondsToNextStop != null && i != lineStops.size() - 1) {
                secondsSinceStart += secondsToNextStop;
            }

            if (lineStop.getBusStop().getId() == busStopId) {
                arrivals.add(Duration.ofSeconds(secondsSinceStart));
            }

        }
        return arrivals;
    }

    /**
     * Return all distinct schedules that this relate to this bus stop.
     */
    private List<ScheduleBO> getRelevantSchedules(BusStop busStop) {
        List<Long> lineIds = busStop.getLineStops()
                .stream()
                .map(LineStop::getLine)
                .map(Line::getId)
                .distinct()
                .toList();

        return this.scheduleRepository.findByLineIdIn(lineIds)
                .stream()
                .map(this.scheduleToBoMapper::enrichWithTargetDestination)
                .toList();
    }
}
