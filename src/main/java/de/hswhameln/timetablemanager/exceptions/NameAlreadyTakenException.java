package de.hswhameln.timetablemanager.exceptions;

public class NameAlreadyTakenException extends InvalidArgumentException{

    public NameAlreadyTakenException(String value) {
        super("name", value, "Name is already taken.");
    }
}
