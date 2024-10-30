package com.example.eventmanagement.model;

import com.example.eventmanagement.dto.RSVPDTO;
import com.example.eventmanagement.enums.RSVPStatus;
import com.example.eventmanagement.key.RSVPKey;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RSVP {
    @EmbeddedId
    @Column(nullable = false)
    private RSVPKey id;

    @ManyToOne
    @MapsId("userId")
    private User user;

    @ManyToOne
    @MapsId("eventId")
    private Event event;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private RSVPStatus status;

    public static RSVPDTO toDTO(RSVP rsvp) {
        return new RSVPDTO(rsvp.getUser().getUsername(), rsvp.getEvent().getId(), rsvp.getStatus().toString());
    }
}

