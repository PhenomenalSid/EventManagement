package com.example.eventmanagement.exception;

import com.example.eventmanagement.exception.AdminException.EventsNotFoundException;
import com.example.eventmanagement.exception.AdminException.UsersNotFoundException;
import com.example.eventmanagement.exception.AuthException.AdminRoleNotAllowedException;
import com.example.eventmanagement.exception.AuthException.ForbiddenException;
import com.example.eventmanagement.exception.AuthException.TokenNotValidException;
import com.example.eventmanagement.exception.AuthException.UserNotLoggedInException;
import com.example.eventmanagement.exception.EventException.EventNotFoundException;
import com.example.eventmanagement.exception.EventException.EventsByOrganizerNotFoundException;
import com.example.eventmanagement.exception.RSVPException.RSVPNotFoundException;
import com.example.eventmanagement.exception.RSVPException.RSVPsForEventNotFoundException;
import com.example.eventmanagement.exception.UserException.UserAlreadyExistsException;
import com.example.eventmanagement.exception.UserException.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AdminRoleNotAllowedException.class)
    public ResponseEntity<ErrorResponse> handleAdminRoleNotAllowedException(AdminRoleNotAllowedException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse("Admins cannot be registered!", ex.getMessage()));
    }

    @ExceptionHandler(UserNotLoggedInException.class)
    public ResponseEntity<ErrorResponse> handleUserNotLoggedInException(UserNotLoggedInException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse("User not logged in", ex.getMessage()));
    }

    @ExceptionHandler(TokenNotValidException.class)
    public ResponseEntity<ErrorResponse> handleTokenNotValidException(TokenNotValidException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse("Invalid token", ex.getMessage()));
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ErrorResponse> handleForbiddenException(ForbiddenException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse("Access denied", ex.getMessage()));
    }

    @ExceptionHandler(UsersNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUsersNotFoundException(UsersNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(new ErrorResponse("No users!", ex.getMessage()));
    }

    @ExceptionHandler(EventsNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEventsNotFoundException(EventsNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(new ErrorResponse("No events!", ex.getMessage()));
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFoundException(UserNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse("User not found", ex.getMessage()));
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleUserAlreadyExistsException(UserAlreadyExistsException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponse("User already exist", ex.getMessage()));
    }

    @ExceptionHandler(EventNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEventNotFound(EventNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse("Event not found", ex.getMessage()));
    }

    @ExceptionHandler(EventsByOrganizerNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEventsByOrganizerNotFound(EventsByOrganizerNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse("Events by Organizer not found", ex.getMessage()));
    }

    @ExceptionHandler(RSVPNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleRSVPNotFound(RSVPNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse("Events by Organizer not found", ex.getMessage()));
    }

    @ExceptionHandler(RSVPsForEventNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleRSVPsForEventNotFound(RSVPsForEventNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse("Events by Organizer not found", ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("Internal error", ex.getMessage()));
    }
}
