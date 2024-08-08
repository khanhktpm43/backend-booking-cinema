package com.dev.booking.Exception;


import javax.security.sasl.AuthenticationException;

public class LoggedOutTokenException extends RuntimeException {
    public LoggedOutTokenException(String message){
        super(message);
    }
}
