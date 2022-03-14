package de.hswhameln.timetablemanager.configuration;

import de.hswhameln.timetablemanager.exceptions.DeletionForbiddenException;
import de.hswhameln.timetablemanager.exceptions.InvalidArgumentException;
import de.hswhameln.timetablemanager.exceptions.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class TimetableManagerExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(InvalidArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    protected String handleInvalidArgumentException(InvalidArgumentException exception) {
        return exception.getMessage();

    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    protected String handleNotFoundException(NotFoundException exception) {
        return exception.getMessage();
    }


    @ExceptionHandler(DeletionForbiddenException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ResponseBody
    protected String handleDeletionForbiddenException(DeletionForbiddenException exception) {
        return exception.getMessage();
    }

}
