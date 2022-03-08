package de.hswhameln.timetablemanager.exceptions;

public class LineStopNotFoundException extends NotFoundException{

    public static final String LINE_STOP = "LineStop";

    public LineStopNotFoundException(String field, Object value) {
        super(LINE_STOP, field, value);
    }

    public LineStopNotFoundException(String field, Object value, String reason) {
        super(LINE_STOP, field, value, reason);
    }
}
