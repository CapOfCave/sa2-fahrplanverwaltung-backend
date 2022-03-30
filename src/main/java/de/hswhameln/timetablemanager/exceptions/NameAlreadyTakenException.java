package de.hswhameln.timetablemanager.exceptions;

public class NameAlreadyTakenException extends InvalidArgumentException{

    public NameAlreadyTakenException(String value) {
        super("Name", value, "Name ist belegt.");
    }
}
