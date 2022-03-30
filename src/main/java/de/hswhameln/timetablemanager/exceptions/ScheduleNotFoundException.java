package de.hswhameln.timetablemanager.exceptions;

public class ScheduleNotFoundException extends NotFoundException {
    public ScheduleNotFoundException(String field, Object value) {
        super("Fahrplan", field, value, "Er existiert nicht.");
    }

}
