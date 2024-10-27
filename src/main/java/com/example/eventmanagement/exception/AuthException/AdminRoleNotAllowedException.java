package com.example.eventmanagement.exception.AuthException;

public class AdminRoleNotAllowedException extends RuntimeException{
    public AdminRoleNotAllowedException(String message){
        super(message);
    }
}
