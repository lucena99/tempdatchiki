package ru.psv4.tempdatchiki.restapi;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
public class SystemRestException extends RuntimeException {
    public SystemRestException(String message) {super(message);};
}
