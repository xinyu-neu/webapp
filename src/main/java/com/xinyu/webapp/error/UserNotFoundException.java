package com.xinyu.webapp.error;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(int id) {
        super("Could not find user " + id);
    }
    public UserNotFoundException(String username) {
        super("Could not find user " + username);
    }
}