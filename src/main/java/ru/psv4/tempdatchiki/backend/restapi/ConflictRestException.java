package ru.psv4.tempdatchiki.backend.restapi;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.CONFLICT)
public class ConflictRestException extends RuntimeException {
    public ConflictRestException(String message) {super(message);};
}
