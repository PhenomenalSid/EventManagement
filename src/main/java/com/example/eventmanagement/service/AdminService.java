package com.example.eventmanagement.service;

import com.example.eventmanagement.dto.AuthDTO;
import com.example.eventmanagement.dto.EventDTO;
import com.example.eventmanagement.dto.UserDTO;
import com.example.eventmanagement.enums.RSVPStatus;
import com.example.eventmanagement.enums.Role;
import com.example.eventmanagement.exception.AdminException.EventsNotFoundException;
import com.example.eventmanagement.exception.AdminException.UsersNotFoundException;
import com.example.eventmanagement.exception.EventException.EventNotFoundException;
import com.example.eventmanagement.exception.UserException.UserAlreadyExistsException;
import com.example.eventmanagement.exception.UserException.UserNotFoundException;
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
import java.util.stream.Collectors;

@Service
public class AdminService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private RSVPRepository rsvpRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private CacheService cacheService;

    public List<UserDTO> getAllUsers() {
        List<UserDTO> userDTOList = cacheService.getCachedAllResources(UserDTO.class);
        if (userDTOList != null) {
            return userDTOList;
        }

        List<User> users = userRepository.findAll();

        if (users.isEmpty()) {
            throw new UsersNotFoundException("There are no users present in the database!");
        }

        userDTOList = users.stream().map(User::toDTO).collect(Collectors.toList());
        cacheService.cacheAllResources(userDTOList);
        return userDTOList;
    }

    @Transactional
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User with userId " + userId + " not found!"));
        if (user.getRole().equals(Role.ADMIN)) {
            throw new AccessDeniedException("You cannot delete other admin!");
        }
        if (user.getRole().equals(Role.ORGANIZER)) {
            List<Event> events = user.getEvents();
            events.forEach(event -> {
                cacheService.invalidateCacheForResource(event.getId(), EventDTO.class);
                cacheService.invalidateCacheForAllResources(EventDTO.class);

                List<RSVP> rsvps = event.getRsvps();
                rsvps.forEach(rsvp -> {
                    if (rsvp.getStatus().equals(RSVPStatus.ACCEPTED)) {
                        emailService.sendEventCancellation(rsvp.getUser().getEmail(), event.getName());
                    }
                });
                rsvpRepository.deleteAll(rsvps);
            });
            eventRepository.deleteAll(events);
        }

        userRepository.delete(user);
        cacheService.invalidateCacheForAllResources(UserDTO.class);
    }

    @Transactional
    public UserDTO updateUserRole(Long userId, String role) {

        User existingUser = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User with userId " + userId + " not found!"));
        if (existingUser.getRole().equals(Role.ADMIN)) {
            throw new AccessDeniedException("You cannot update role of other admin!");
        }

        Role newRole = Role.fromString(role);

        if (existingUser.getRole().equals(Role.ORGANIZER) && newRole.equals(Role.PARTICIPANT)) {
            List<Event> events = existingUser.getEvents();
            events.forEach(event -> {
                cacheService.invalidateCacheForResource(event.getId(), EventDTO.class);
                cacheService.invalidateCacheForAllResources(EventDTO.class);

                List<RSVP> rsvps = event.getRsvps();
                rsvps.forEach(rsvp -> {
                    if (rsvp.getStatus().equals(RSVPStatus.ACCEPTED)) {
                        emailService.sendEventCancellation(rsvp.getUser().getEmail(), event.getName());
                    }
                });
                rsvpRepository.deleteAll(rsvps);
            });
            eventRepository.deleteAll(events);
        }

        existingUser.setRole(Role.fromString(role));
        User user = userRepository.save(existingUser);

        UserDTO userDTO = User.toDTO(user);
        cacheService.invalidateCacheForAllResources(UserDTO.class);

        return userDTO;
    }

    public List<EventDTO> getAllEvents() {
        List<EventDTO> eventDTOList = cacheService.getCachedAllResources(EventDTO.class);
        if (eventDTOList != null) {
            return eventDTOList;
        }
        List<Event> events = eventRepository.findAll();

        if (events.isEmpty()) {
            throw new EventsNotFoundException("There are no events present in the database!");
        }

        eventDTOList = events.stream().map(Event::toDTO).collect(Collectors.toList());
        cacheService.cacheAllResources(eventDTOList);
        return eventDTOList;
    }

    public void deleteEvent(Long eventId) {
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new EventNotFoundException("Event with eventId " + eventId + " not found!"));
        if (!event.getOrganizer().getRole().equals(Role.ADMIN)) {
            throw new AccessDeniedException("You cannot delete events organized by other admin!");
        }

        List<RSVP> rsvps = event.getRsvps();
        rsvps.forEach(rsvp -> {
            if (rsvp.getStatus().equals(RSVPStatus.ACCEPTED)) {
                emailService.sendEventCancellation(rsvp.getUser().getEmail(), event.getName());
            }
        });
        rsvpRepository.deleteAll(rsvps);

        User user = userRepository.findById(event.getOrganizer().getId()).get();

        user.getEvents().removeIf(event1 -> event1.getId().equals(eventId));

        userRepository.save(user);

        eventRepository.delete(event);

        cacheService.invalidateCacheForResource(eventId, EventDTO.class);
        cacheService.invalidateCacheForAllResources(EventDTO.class);
        cacheService.invalidateCacheForAllResources(UserDTO.class);
    }

    public UserDTO createUser(AuthDTO authDTO) {
        if (userRepository.findByUsername(authDTO.getUsername()).isPresent()) {
            throw new UserAlreadyExistsException("Admin with username " + authDTO.getUsername() + " already exist!");
        }
        User user = AuthDTO.toEntity(authDTO);
        User savedUser = userRepository.save(user);
        cacheService.invalidateCacheForAllResources(UserDTO.class);
        return User.toDTO(savedUser);
    }
}
