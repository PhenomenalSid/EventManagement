package com.example.eventmanagement.repository;

import com.example.eventmanagement.model.Event;
import com.example.eventmanagement.model.RSVP;
import com.example.eventmanagement.key.RSVPKey;
import com.example.eventmanagement.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RSVPRepository extends JpaRepository<RSVP, RSVPKey> {
    Optional<RSVP> findByUserAndEvent(User user, Event event);
    List<RSVP> findByEventId(Long eventId);
}

