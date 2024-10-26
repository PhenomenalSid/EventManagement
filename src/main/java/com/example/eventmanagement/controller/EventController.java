package com.example.eventmanagement.controller;

import com.example.eventmanagement.dto.EventDTO;
import com.example.eventmanagement.dto.UserDTO;
import com.example.eventmanagement.model.User;
import com.example.eventmanagement.service.EventService;
import com.example.eventmanagement.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/events")
public class EventController {
    @Autowired
    private EventService eventService;

    @Autowired
    private UserService userService;

    @PostMapping("/create-event")
    public ResponseEntity<EventDTO> createEvent(@RequestBody EventDTO eventDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userService.findUserByUsername(username);
        EventDTO event = eventService.createEvent(user, eventDTO);
        return ResponseEntity.ok(event);
    }

    @GetMapping("/organizer/{id}")
    public ResponseEntity<List<EventDTO>> getAllEventsByOrganizer(@PathVariable Long id) {
        List<EventDTO> events = eventService.getAllEventsByOrganizer(id);
        return ResponseEntity.ok(events);
    }

    @GetMapping("/event/{eventId}")
    public ResponseEntity<EventDTO> getEventById(@PathVariable Long eventId){
        EventDTO event = eventService.getEventById(eventId);
        return ResponseEntity.ok(event);
    }

    @PutMapping("/event/{eventId}")
    public ResponseEntity<EventDTO> updateEvent(@PathVariable Long eventId, @RequestBody EventDTO eventDTO){
        EventDTO event = eventService.updateEvent(eventId, eventDTO);
        return ResponseEntity.ok(event);
    }

    @DeleteMapping("/event/{eventId}")
    public ResponseEntity<String> deleteEvent(@PathVariable Long eventId){
        eventService.deleteEvent(eventId);
        return ResponseEntity.ok("Deleted Successfully!");
    }
}

