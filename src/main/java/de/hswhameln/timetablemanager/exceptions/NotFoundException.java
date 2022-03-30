package de.hswhameln.timetablemanager.exceptions;

public class NotFoundException extends Exception {

    public NotFoundException(String type, String field, Object value, String reason) {
        super(String.format("%s mit %s '%s' konnte nicht gefunden werden. Grund: %s", type, field, value, reason));
    }
}
