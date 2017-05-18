package com.softjourn.exception;


public class AccountCreationException extends RuntimeException {

    public AccountCreationException(String message) {
        super(message);
    }

    public AccountCreationException(String msg, Exception e) {
        super(msg, e);
    }
}
