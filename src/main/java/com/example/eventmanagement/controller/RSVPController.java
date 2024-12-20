package com.example.eventmanagement.controller;

import com.example.eventmanagement.dto.RSVPDTO;
import com.example.eventmanagement.service.RSVPService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rsvp")
public class RSVPController {

    @Autowired
    private RSVPService rsvpService;

    @PostMapping("/respond")
    public ResponseEntity<RSVPDTO> respondToEvent(@RequestBody RSVPDTO rsvpDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        RSVPDTO rsvp = rsvpService.respondToEvent(username, rsvpDTO);
        return ResponseEntity.ok(rsvp);
    }

    @GetMapping("/event/{eventId}")
    public ResponseEntity<List<RSVPDTO>> getRSVPsForEvent(@PathVariable Long eventId) {
        List<RSVPDTO> rsvps = rsvpService.getRSVPsForEvent(eventId);
        return ResponseEntity.ok(rsvps);
    }

    @PutMapping("")
    public ResponseEntity<RSVPDTO> updateRSVP(
            @RequestParam Long userId,
            @RequestParam Long eventId,
            @RequestBody RSVPDTO rsvpdto) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        RSVPDTO rsvp = rsvpService.updateRSVP(userId, eventId, rsvpdto, username);
        return ResponseEntity.ok(rsvp);
    }

    @DeleteMapping("")
    public ResponseEntity<String> deleteRSVP(
            @RequestParam Long userId,
            @RequestParam Long eventId) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        rsvpService.deleteRSVP(userId, eventId, username);
        return ResponseEntity.ok("Deleted RSVP successfully!");
    }
}
