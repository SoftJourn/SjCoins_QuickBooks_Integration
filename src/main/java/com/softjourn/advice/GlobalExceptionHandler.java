package com.softjourn.advice;

import com.softjourn.exception.AccountCreationException;
import com.softjourn.exception.SjCoinsCredentialsMissingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Collections;
import java.util.Map;


@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(AccountCreationException.class)
    public Map<String, String> accountCreationExceptionHandler(AccountCreationException e) {
        log.error(e.getLocalizedMessage(), e);

        return Collections.singletonMap("Error", e.getLocalizedMessage());
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(SjCoinsCredentialsMissingException.class)
    public Map<String, String> sjCoinsCredentialMissingExceptionHandler(SjCoinsCredentialsMissingException e) {
        log.error(e.getLocalizedMessage(), e);

        return Collections.singletonMap("Error", e.getLocalizedMessage());
    }
}
