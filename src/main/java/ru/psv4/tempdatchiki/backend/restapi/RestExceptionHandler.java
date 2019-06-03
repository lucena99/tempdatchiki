package ru.psv4.tempdatchiki.backend.restapi;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(NotFoundRestException.class)
    protected ResponseEntity<RestException> handleThereIsNoSuchElementException(NotFoundRestException e) {
        return new ResponseEntity<>(new RestException(e.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(SystemRestException.class)
    protected ResponseEntity<RestException> handleSystemException(SystemRestException e) {
        return new ResponseEntity<>(new RestException(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ConflictRestException.class)
    protected ResponseEntity<RestException> handleConflictException(ConflictRestException e) {
        return new ResponseEntity<>(new RestException(e.getMessage()), HttpStatus.CONFLICT);
    }

    private static class RestException {
        private String message;
        public RestException(String message) {this.message = message;}

        @Override
        public String toString() {
            return message;
        }
    }
}
