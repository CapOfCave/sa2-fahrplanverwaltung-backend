package de.hswhameln.timetablemanager.exceptions;

public class InvalidArgumentException extends Exception {

    public InvalidArgumentException(String argumentName, String value, String reason) {
        super(String.format("The value '%s' is invalid for argument '%s'. Reason: %s", value, argumentName, reason));
    }

}
