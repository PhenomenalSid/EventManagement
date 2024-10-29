package com.example.eventmanagement.service;

import com.example.eventmanagement.dto.EventDTO;
import com.example.eventmanagement.dto.EventStatsDTO;
import com.example.eventmanagement.enums.RSVPStatus;
import com.example.eventmanagement.exception.EventException.EventNotFoundException;
import com.example.eventmanagement.exception.EventException.EventsByOrganizerNotFoundException;
import com.example.eventmanagement.model.Event;
import com.example.eventmanagement.model.RSVP;
import com.example.eventmanagement.model.User;
import com.example.eventmanagement.repository.EventRepository;
import com.example.eventmanagement.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
public class EventService {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    @Scheduled(cron = "0 0 * * * *")
    public void sendReminders() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime reminderTime = now.plusHours(2);

        List<Event> upcomingEvents = eventRepository.findAllByDateBetween(now, reminderTime);

        for (Event event : upcomingEvents) {
            List<RSVP> acceptedRSVPs = event.getRsvps().stream()
                    .filter(rsvp -> rsvp.getStatus() == RSVPStatus.ACCEPTED)
                    .toList();

            for (RSVP rsvp : acceptedRSVPs) {
                emailService.sendEmailReminder(rsvp.getUser().getUsername(), event);
            }
        }
    }

    @Transactional
    public EventDTO createEvent(String username, EventDTO eventDTO) {
        Event event = EventDTO.toEntity(eventDTO);
        User user = userRepository.findByUsername(username).get();
        event.setOrganizer(user);
        Event savedEvent = eventRepository.save(event);
        user.getEvents().add(savedEvent);
        userRepository.save(user);
        return Event.toDTO(savedEvent);
    }

    public List<EventDTO> getAllEventsByOrganizer(Long organizerId) {
        List<Event> events = eventRepository.findByOrganizerId(organizerId);
        if (events.isEmpty()) {
            throw new EventsByOrganizerNotFoundException("Event by Organizer with id " + organizerId + " not found!");
        }
        return events.stream().map(Event::toDTO).collect(Collectors.toList());
    }

    public EventDTO getEventById(Long eventId) {
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new EventNotFoundException("Event with id " + eventId + " not found"));
        return Event.toDTO(event);
    }

    @Transactional
    public EventDTO updateEvent(Long eventId, EventDTO eventDTO, String username) {
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new EventNotFoundException("Event with id " + eventId + " not found"));

        if (!event.getOrganizer().getUsername().equals(username))
            throw new AccessDeniedException("You are not allowed to update event of other organizer's event!");

        event.setName(eventDTO.getName() == null ? event.getName() : eventDTO.getName());
        event.setLocation(eventDTO.getLocation() == null ? event.getLocation() : eventDTO.getLocation());
        event.setDate(eventDTO.getDate() == null ? event.getDate() : LocalDateTime.parse(eventDTO.getDate()));
        event.setDescription(eventDTO.getDescription() == null ? event.getDescription() : eventDTO.getDescription());
        Event savedEvent = eventRepository.save(event);

        event.getRsvps().forEach(rsvp -> {
            if (rsvp.getStatus().equals(RSVPStatus.ACCEPTED)) {
                emailService.sendEventUpdateNotification(rsvp.getUser().getUsername(), event.getName());
            }
        });

        return Event.toDTO(savedEvent);
    }

    @Transactional
    public void deleteEvent(Long eventId, String username) {
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new EventNotFoundException("Even with eventId " + eventId + " not found!"));
        if (!event.getOrganizer().getUsername().equals(username))
            throw new AccessDeniedException("You are not allowed to delete event of other organizer's event!");

        event.getRsvps().forEach(rsvp -> {
            if (rsvp.getStatus().equals(RSVPStatus.ACCEPTED)) {
                emailService.sendEventCancellation(rsvp.getUser().getUsername(), event.getName());
            }
        });

        eventRepository.delete(event);
    }

    public EventStatsDTO getEventStatsById(Long eventId, String username) {
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new EventNotFoundException("Even with eventId " + eventId + " not found!"));
        if (!event.getOrganizer().getUsername().equals(username))
            throw new AccessDeniedException("You are not allowed to see events stats for other organizer's event");
        AtomicReference<Long> acceptedRSVPs = new AtomicReference<>(0L);
        AtomicReference<Long> rejectedRSVPs = new AtomicReference<>(0L);
        AtomicReference<Long> pendingRSVPs = new AtomicReference<>(0L);
        event.getRsvps().forEach(rsvp -> {
            if (rsvp.getStatus() == RSVPStatus.ACCEPTED) {
                acceptedRSVPs.getAndSet(acceptedRSVPs.get() + 1);
            } else if (rsvp.getStatus() == RSVPStatus.PENDING) {
                pendingRSVPs.getAndSet(pendingRSVPs.get() + 1);
            } else if (rsvp.getStatus() == RSVPStatus.DECLINED) {
                rejectedRSVPs.getAndSet(rejectedRSVPs.get() + 1);
            }
        });
        return new EventStatsDTO(eventId, event.getOrganizer().getUsername(), (long) event.getRsvps().size(), acceptedRSVPs.get(), pendingRSVPs.get(), rejectedRSVPs.get());
    }
}

