package com.example.eventmanagement.service;

import com.example.eventmanagement.dto.EventDTO;
import com.example.eventmanagement.dto.UserDTO;
import com.example.eventmanagement.enums.Role;
import com.example.eventmanagement.exception.AdminException.EventsNotFoundException;
import com.example.eventmanagement.exception.AdminException.UsersNotFoundException;
import com.example.eventmanagement.exception.EventException.EventNotFoundException;
import com.example.eventmanagement.exception.UserException.UserNotFoundException;
import com.example.eventmanagement.model.Event;
import com.example.eventmanagement.model.User;
import com.example.eventmanagement.repository.EventRepository;
import com.example.eventmanagement.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EventRepository eventRepository;

    public List<UserDTO> getAllUsers() {
        List<User> users = userRepository.findAll();

        if (users.isEmpty()) {
            throw new UsersNotFoundException("There are no users present in the database!");
        }

        return users.stream().map(User::toDTO).collect(Collectors.toList());
    }

    @Transactional
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User with userId " + userId + " not found!"));
        if (user.getRole().equals(Role.ADMIN)) {
            throw new AccessDeniedException("You cannot delete other admin!");
        }
        userRepository.delete(user);
    }

    @Transactional
    public UserDTO updateUserRole(Long userId, String role) {

        User existingUser = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User with userId " + userId + " not found!"));
        if (existingUser.getRole().equals(Role.ADMIN)) {
            throw new AccessDeniedException("You cannot update role of other admin!");
        }

        Role newRole = Role.fromString(role);

        if (existingUser.getRole().equals(Role.ORGANIZER) && newRole.equals(Role.PARTICIPANT)) {
            List<Event> events = eventRepository.findByOrganizerId(existingUser.getId());
            eventRepository.deleteAll(events);
            existingUser.setEvents(new ArrayList<>());
        }

        existingUser.setRole(Role.fromString(role));
        User user = userRepository.save(existingUser);
        return User.toDTO(user);
    }

    public List<EventDTO> getAllEvents() {
        List<Event> events = eventRepository.findAll();

        if (events.isEmpty()) {
            throw new EventsNotFoundException("There are no events present in the database!");
        }

        return events.stream().map(Event::toDTO).collect(Collectors.toList());
    }

    public void deleteEvent(Long eventId) {
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new EventNotFoundException("Event with eventId " + eventId + " not found!"));
        if (!event.getOrganizer().getRole().equals(Role.ADMIN)) {
            throw new AccessDeniedException("You cannot delete events organized by other admin!");
        }
        eventRepository.delete(event);
    }
}
