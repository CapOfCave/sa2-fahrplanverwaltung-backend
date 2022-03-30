package de.hswhameln.timetablemanager.exceptions;

public class InvalidArgumentException extends Exception {

    public InvalidArgumentException(String argumentName, Object value, String reason) {
        super(String.format("Der Wert '%s' ist ungültig für Argument '%s'. Grund: %s", value, argumentName, reason));
    }

}
