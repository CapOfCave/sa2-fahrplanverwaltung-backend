package de.hswhameln.timetablemanager.exceptions;

public class BusStopNotFoundException extends NotFoundException{

    public BusStopNotFoundException(String field, Object value) {
        super("Bushaltestelle", field, String.valueOf(value), "Sie existiert nicht.");
    }
}
