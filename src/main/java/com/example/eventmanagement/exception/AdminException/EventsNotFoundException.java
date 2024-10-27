package com.example.eventmanagement.exception.AdminException;

public class EventsNotFoundException extends RuntimeException {
    public EventsNotFoundException(String message) {
        super(message);
    }
}
