package de.hswhameln.timetablemanager.exceptions;

public class LineNotFoundException extends NotFoundException{
    public LineNotFoundException(String field, Object value) {
        super("Line", field, value);
    }
}
