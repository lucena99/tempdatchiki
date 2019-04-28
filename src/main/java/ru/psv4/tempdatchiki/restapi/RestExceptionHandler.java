package ru.psv4.tempdatchiki.restapi;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(NotFoundRestException.class)
    protected ResponseEntity<SimpleException> handleThereIsNoSuchElementException(NotFoundRestException e) {
        return new ResponseEntity<>(new SimpleException("There is no such element"), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(SystemRestException.class)
    protected ResponseEntity<SimpleException> handleSystemException(SystemRestException e) {
        return new ResponseEntity<>(new SimpleException("System error"), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private static class SimpleException {
        private String message;
        public SimpleException(String message) {this.message = message;}
    }
}
