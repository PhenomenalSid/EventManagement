package com.example.eventmanagement.enums;

import lombok.Getter;

public enum Role {
    PARTICIPANT("participant"),
    ORGANIZER("organizer"),
    ADMIN("admin");

    @Getter
    private final String role;

    private Role(String role){
        this.role = role;
    }

    public static Role fromString(String role) {
        for (Role r : Role.values()) {
            if (r.role.equalsIgnoreCase(role)) {
                return r;
            }
        }
        throw new IllegalArgumentException("No enum constant for role: " + role);
    }
}
