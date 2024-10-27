package com.example.eventmanagement.service;

import com.example.eventmanagement.dto.RSVPDTO;
import com.example.eventmanagement.enums.RSVPStatus;
import com.example.eventmanagement.exception.EventException.EventNotFoundException;
import com.example.eventmanagement.exception.RSVPException.RSVPNotFoundException;
import com.example.eventmanagement.exception.RSVPException.RSVPsForEventNotFoundException;
import com.example.eventmanagement.key.RSVPKey;
import com.example.eventmanagement.model.Event;
import com.example.eventmanagement.model.RSVP;
import com.example.eventmanagement.model.User;
import com.example.eventmanagement.repository.EventRepository;
import com.example.eventmanagement.repository.RSVPRepository;
import com.example.eventmanagement.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RSVPService {

    @Autowired
    private RSVPRepository rsvpRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public RSVPDTO respondToEvent(String username, RSVPDTO rsvpDTO) {
        Event event = eventRepository.findById(rsvpDTO.getEventId()).orElseThrow(() -> new EventNotFoundException("Event with eventId " + rsvpDTO.getEventId() + " not found!"));
        User user = userRepository.findByUsername(username).get();
        Optional<RSVP> existingRSVP = rsvpRepository.findByUserAndEvent(user, event);
        RSVP rsvp;

        if (existingRSVP.isPresent()) {
            rsvp = existingRSVP.get();
            if (!rsvp.getUser().getUsername().equals(username)) {
                throw new AccessDeniedException("You are not allowed to update other user's rsvp!");
            }
            rsvp.setStatus(RSVPStatus.fromString(rsvpDTO.getStatus()));
        } else {
            RSVPKey rsvpKey = new RSVPKey();
            rsvpKey.setEventId(event.getId());
            rsvpKey.setUserId(user.getId());

            rsvp = new RSVP();
            rsvp.setId(rsvpKey);
            rsvp.setUser(user);
            rsvp.setEvent(event);
            rsvp.setStatus(RSVPStatus.fromString(rsvpDTO.getStatus()));
        }

        RSVP savedRSVP = rsvpRepository.save(rsvp);
        event.getRsvps().add(savedRSVP);
        eventRepository.save(event);
        return RSVP.toDTO(savedRSVP);
    }

    public List<RSVPDTO> getRSVPsForEvent(Long eventId) {
        List<RSVP> rsvps = rsvpRepository.findByEventId(eventId);
        if (rsvps.isEmpty()) {
            throw new RSVPsForEventNotFoundException("RSVPs for event " + eventId + " not found!");
        } else {
            return rsvps.stream().map(RSVP::toDTO).collect(Collectors.toList());
        }
    }

    @Transactional
    public RSVPDTO updateRSVP(Long userId, Long eventId, RSVPDTO rsvpRequestDTO, String username) {
        RSVPKey rsvpKey = new RSVPKey(userId, eventId);
        RSVP existingRSVP = rsvpRepository.findById(rsvpKey).orElseThrow(() -> new RSVPNotFoundException("RSVP not found!"));
        if (!existingRSVP.getUser().getUsername().equals(username)) {
            throw new AccessDeniedException("You are not allowed to update other user's rsvp!");
        }
        existingRSVP.setStatus(RSVPStatus.fromString(rsvpRequestDTO.getStatus()));
        RSVP savedRSVP = rsvpRepository.save(existingRSVP);
        return RSVP.toDTO(savedRSVP);
    }

    @Transactional
    public void deleteRSVP(Long userId, Long eventId, RSVPDTO rsvpdto, String username) {
        RSVPKey rsvpKey = new RSVPKey(userId, eventId);
        RSVP existingRSVP = rsvpRepository.findById(rsvpKey).orElseThrow(() -> new RSVPNotFoundException("RSVP not found!"));
        if (!existingRSVP.getUser().getUsername().equals(username)) {
            throw new AccessDeniedException("You are not allowed to delete other user's rsvp!");
        }
        rsvpRepository.delete(existingRSVP);
    }
}

