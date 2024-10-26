package com.example.eventmanagement.enums;

import lombok.Getter;

@Getter
public enum RSVPStatus {
    ACCEPTED("accepted"),
    PENDING("pending"),
    DECLINED("declined");

    private final String status;

    private RSVPStatus(String status) {
        this.status = status;
    }

    public static RSVPStatus fromString(String status) {
        for (RSVPStatus r : RSVPStatus.values()) {
            if (r.status.equalsIgnoreCase(status)) {
                return r;
            }
        }
        throw new IllegalArgumentException("No enum constant for rsvp status: " + status);
    }
}
