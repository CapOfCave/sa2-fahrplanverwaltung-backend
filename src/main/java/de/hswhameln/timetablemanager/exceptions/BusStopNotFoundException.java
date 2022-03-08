package de.hswhameln.timetablemanager.exceptions;

public class BusStopNotFoundException extends NotFoundException{
    public BusStopNotFoundException(String field, Object value) {
        super("BusStop", field, String.valueOf(value));
    }
}
