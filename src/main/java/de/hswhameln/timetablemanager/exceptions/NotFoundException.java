package de.hswhameln.timetablemanager.exceptions;

public class NotFoundException extends Exception {
    public NotFoundException(String type, String field, Object value) {
        this(type, field, value, "It does not exist.");
    }

    public NotFoundException(String type, String field, Object value, String reason) {
        super(String.format("%s with %s '%s' was not found. Reason: %s", type, field, value, reason));
    }
}
