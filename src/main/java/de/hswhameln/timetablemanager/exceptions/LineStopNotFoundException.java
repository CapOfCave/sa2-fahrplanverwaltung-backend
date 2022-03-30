package de.hswhameln.timetablemanager.exceptions;

public class LineStopNotFoundException extends NotFoundException{

    public static final String LINE_STOP = "Buslinienhalt";

    public LineStopNotFoundException(String field, Object value) {
        super(LINE_STOP, field, value, "Er existiert nicht.");
    }

    public LineStopNotFoundException(String field, Object value, String reason) {
        super(LINE_STOP, field, value, reason);
    }
}
