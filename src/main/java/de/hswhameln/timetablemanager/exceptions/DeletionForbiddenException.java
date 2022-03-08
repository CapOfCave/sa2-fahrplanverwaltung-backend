package de.hswhameln.timetablemanager.exceptions;

public class DeletionForbiddenException extends Exception{
    public DeletionForbiddenException(String type, Object id, String reason) {
        super(String.format("Could not delete %s with id %s. Reason: %s", type, id, reason));
    }
}
