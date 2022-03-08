package de.hswhameln.timetablemanager.configuration;

import de.hswhameln.timetablemanager.exceptions.InvalidArgumentException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class TimetableManagerExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(InvalidArgumentException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    @ResponseBody
    protected String handleInvalidArgument(InvalidArgumentException exception) {
        return exception.getMessage();

    }
}
