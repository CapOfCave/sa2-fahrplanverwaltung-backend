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

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
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

    public BusStopScheduleBO getBusStopSchedule(long id) {
        BusStop busStop = this.busStopRepository.findById(id).orElseThrow();
        List<Long> lineIds = busStop.getLineStops()
                .stream()
                .map(LineStop::getLine)
                .map(Line::getId)
                .distinct()
                .toList();

        List<Schedule> relevantSchedules = this.scheduleRepository.findByLineIdIn(lineIds);

        Collection<BusStopScheduleEntryBO> scheduleEntries = new ArrayList<>();

        for (Schedule schedule : relevantSchedules) {
            if (schedule.isReverseDirection()) {
                System.out.println("Reverse direction not yet implemented. Ignoring schedule");
                continue;
            }
            ScheduleBO scheduleBO = this.scheduleToBoMapper.enrichWithTargetDestination(schedule);

            Line line = schedule.getLine();
            List<LineStop> lineStops = line.getLineStops();
            int secondsSinceStart = 0;
            for (int i = 0; i < lineStops.size(); i++) {
                LineStop lineStop = lineStops.get(i);
                if (lineStop.getBusStop().getId() == id) {
                    LocalTime arrival = schedule.getStartTime().plus(secondsSinceStart, ChronoUnit.SECONDS);
                    BusStopScheduleEntryBO busStopScheduleEntryBO = new BusStopScheduleEntryBO(busStop, scheduleBO, arrival);
                    scheduleEntries.add(busStopScheduleEntryBO);
                }

                Integer secondsToNextStop = lineStop.getSecondsToNextStop();
                if (secondsToNextStop != null) {
                    secondsSinceStart += secondsToNextStop;
                }
            }
        }

        return new BusStopScheduleBO(busStop, scheduleEntries);
    }
}
