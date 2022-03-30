package de.hswhameln.timetablemanager.exceptions;

public class DeletionForbiddenException extends Exception{
    public DeletionForbiddenException(String type, Object id, String reason) {
        super(String.format("%s mit ID %s konnte nicht gelöscht werden. Grund: %s", type, id, reason));
    }
}
