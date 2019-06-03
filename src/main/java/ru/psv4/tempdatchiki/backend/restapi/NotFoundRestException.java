package ru.psv4.tempdatchiki.backend.restapi;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND)
public class NotFoundRestException extends RuntimeException {
    public NotFoundRestException(String message) {super(message);};
}
