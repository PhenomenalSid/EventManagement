package com.example.eventmanagement.exception.EventException;

public class EventsByOrganizerNotFoundException extends RuntimeException {
    public EventsByOrganizerNotFoundException(String message) {
        super(message);
    }
}
