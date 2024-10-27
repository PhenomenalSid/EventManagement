package com.example.eventmanagement.controller;

import com.example.eventmanagement.dto.EventDTO;
import com.example.eventmanagement.dto.EventStatsDTO;
import com.example.eventmanagement.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/events")
public class EventController {
    @Autowired
    private EventService eventService;

    @PostMapping("/create-event")
    public ResponseEntity<EventDTO> createEvent(@RequestBody EventDTO eventDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        EventDTO event = eventService.createEvent(username, eventDTO);
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

    @GetMapping("/event-stats/{eventId}")
    public ResponseEntity<EventStatsDTO> getEventStatsById(@PathVariable Long eventId){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        EventStatsDTO eventStatsDTO = eventService.getEventStatsById(eventId, username);
        return ResponseEntity.ok(eventStatsDTO);
    }

    @PutMapping("/event/{eventId}")
    public ResponseEntity<EventDTO> updateEvent(@PathVariable Long eventId, @RequestBody EventDTO eventDTO){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        EventDTO event = eventService.updateEvent(eventId, eventDTO, username);
        return ResponseEntity.ok(event);
    }

    @DeleteMapping("/event/{eventId}")
    public ResponseEntity<String> deleteEvent(@PathVariable Long eventId){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        eventService.deleteEvent(eventId, username);
        return ResponseEntity.ok("Deleted Successfully!");
    }
}

