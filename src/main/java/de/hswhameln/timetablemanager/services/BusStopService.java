package de.hswhameln.timetablemanager.services;

import de.hswhameln.timetablemanager.businessobjects.BusStopScheduleEntryBO;
import de.hswhameln.timetablemanager.businessobjects.BusStopSchedulesBO;
import de.hswhameln.timetablemanager.businessobjects.BusStopTimetableBO;
import de.hswhameln.timetablemanager.businessobjects.BusStopTimetableEntryBO;
import de.hswhameln.timetablemanager.businessobjects.ScheduleBO;
import de.hswhameln.timetablemanager.entities.BusStop;
import de.hswhameln.timetablemanager.entities.Line;
import de.hswhameln.timetablemanager.entities.LineStop;
import de.hswhameln.timetablemanager.exceptions.BusStopNotFoundException;
import de.hswhameln.timetablemanager.exceptions.DeletionForbiddenException;
import de.hswhameln.timetablemanager.exceptions.LineNotFoundException;
import de.hswhameln.timetablemanager.exceptions.NameAlreadyTakenException;
import de.hswhameln.timetablemanager.mapping.ScheduleToBoMapper;
import de.hswhameln.timetablemanager.repositories.BusStopRepository;
import de.hswhameln.timetablemanager.repositories.LineRepository;
import de.hswhameln.timetablemanager.repositories.ScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Service
public class BusStopService {

    private final BusStopRepository busStopRepository;

    private final LineRepository lineRepository;

    private final ScheduleRepository scheduleRepository;

    private final ScheduleToBoMapper scheduleToBoMapper;

    @Autowired
    public BusStopService(BusStopRepository busStopRepository, LineRepository lineRepository, ScheduleRepository scheduleRepository, ScheduleToBoMapper scheduleToBoMapper) {
        this.busStopRepository = busStopRepository;
        this.lineRepository = lineRepository;
        this.scheduleRepository = scheduleRepository;
        this.scheduleToBoMapper = scheduleToBoMapper;
    }

    public BusStop createBusStop(String name) throws NameAlreadyTakenException {
        if (this.busStopRepository.existsByName(name)) {
            throw new NameAlreadyTakenException(name);
        }
        var busStop = new BusStop(name);
        return this.busStopRepository.save(busStop);
    }

    public Collection<BusStop> getBusStops() {
        return new ArrayList<>(this.busStopRepository.findAll());
    }

    @Transactional
    public void deleteBusStop(long id) throws BusStopNotFoundException, DeletionForbiddenException {
        BusStop busStop = getBusStop(id);
        if (!busStop.getLineStops().isEmpty()) {
            throw new DeletionForbiddenException("BusStop", id, "This BusStop is part of at least one line.");
        }
        this.busStopRepository.deleteById(id);
    }

    public BusStop modifyBusStop(long id, String name) throws BusStopNotFoundException {
        BusStop busStop = getBusStop(id);
        busStop.setName(name);
        return this.busStopRepository.save(busStop);
    }

    public BusStop getBusStop(long id) throws BusStopNotFoundException {
        return this.busStopRepository.findById(id).orElseThrow(() -> new BusStopNotFoundException("busStopId", id));
    }

    /**
     * Return all bus lines that stop at this bus stop combined with their arrival time (in order of arrival)
     */
    @Transactional
    public BusStopSchedulesBO getBusStopSchedule(long id) throws BusStopNotFoundException {
        BusStop busStop = this.getBusStop(id);

        List<BusStopScheduleEntryBO> busStopScheduleEntries = getBusStopScheduleEntries(busStop);

        return new BusStopSchedulesBO(busStop, busStopScheduleEntries);
    }

    @Transactional
    public BusStopTimetableBO getTimetable(long busStopId, LocalDateTime startTime, Duration duration) throws BusStopNotFoundException {
        BusStop busStop = this.getBusStop(busStopId);
        List<BusStopScheduleEntryBO> busStopScheduleEntries = getBusStopScheduleEntries(busStop);

        record DayAndScheduleEntries(int dayOffset, List<BusStopScheduleEntryBO> busStopScheduleEntries) {
            Stream<BusStopTimetableEntryBO> toDayAndScheduleEntryStream(LocalDateTime startTime) {
                return this.busStopScheduleEntries.stream()
                        .map(busStopScheduleEntryBO ->
                                new BusStopTimetableEntryBO(busStopScheduleEntryBO.getSchedule(), toLocalDateTime(startTime, this.dayOffset, busStopScheduleEntryBO.getArrival())));
            }

            private static LocalDateTime toLocalDateTime(LocalDateTime startTime, int dayOffset, LocalTime timeOfDay) {
                return LocalDateTime.of(startTime.toLocalDate(), timeOfDay).plusDays(dayOffset);
            }
        }

        // stream all busStopScheduleEntries x times with an ever-increasing counter dayOffset until its arrival is after the end time
        List<BusStopTimetableEntryBO> allDayAndScheduleEntries = IntStream.iterate(0, dayOffset -> dayOffset + 1)
                .mapToObj(dayOffset -> new DayAndScheduleEntries(dayOffset, busStopScheduleEntries))
                .flatMap(dayAndScheduleEntries -> dayAndScheduleEntries.toDayAndScheduleEntryStream(startTime))
                .dropWhile(dayAndScheduleEntry -> dayAndScheduleEntry.getArrival().isBefore(startTime))
                .takeWhile(dayAndScheduleEntry -> !dayAndScheduleEntry.getArrival().isAfter(startTime.plus(duration)))
                .toList();
        return new BusStopTimetableBO(busStop, allDayAndScheduleEntries);

    }

    @Transactional
    public BusStopSchedulesBO getSchedulesForLineAtBusStop(long busStopId, long lineId) throws BusStopNotFoundException, LineNotFoundException {
        BusStop busStop = this.getBusStop(busStopId);

        List<BusStopScheduleEntryBO> busStopScheduleEntries = this.lineRepository.findById(lineId)
                .orElseThrow(() -> new LineNotFoundException("lineId", lineId))
                .getSchedules()
                .stream()
                .map(this.scheduleToBoMapper::enrichWithTargetDestination)
                .map(schedule -> getBusStopScheduleEntriesForSchedule(busStopId, schedule))
                .flatMap(Collection::stream)
                .sorted(Comparator.comparing(BusStopScheduleEntryBO::getArrival))
                .toList();
        return new BusStopSchedulesBO(busStop, busStopScheduleEntries);


    }

    /**
     * Return all Times a bus will arrive at this stop in a given day, in order of arrival time
     */
    private List<BusStopScheduleEntryBO> getBusStopScheduleEntries(BusStop busStop) {

        return getRelevantSchedules(busStop)
                .stream()
                .map(schedule -> getBusStopScheduleEntriesForSchedule(busStop.getId(), schedule))
                .flatMap(Collection::stream)
                .sorted(Comparator.comparing(BusStopScheduleEntryBO::getArrival))
                .toList();
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
