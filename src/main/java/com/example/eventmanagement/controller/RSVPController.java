package com.example.eventmanagement.controller;

import com.example.eventmanagement.dto.RSVPDTO;
import com.example.eventmanagement.key.RSVPKey;
import com.example.eventmanagement.model.User;
import com.example.eventmanagement.service.RSVPService;
import com.example.eventmanagement.service.UserService;
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

    @Autowired
    private UserService userService;

    @PostMapping("/respond")
    public ResponseEntity<RSVPDTO> respondToEvent(@RequestBody RSVPDTO rsvpDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userService.findUserByUsername(username);
        RSVPDTO rsvp = rsvpService.respondToEvent(user, rsvpDTO);
        return ResponseEntity.ok(rsvp);
    }

    @GetMapping("/event/{eventId}")
    public ResponseEntity<List<RSVPDTO>> getRSVPsForEvent(@PathVariable Long eventId) {
        List<RSVPDTO> rsvps = rsvpService.getRSVPsForEvent(eventId);
        return ResponseEntity.ok(rsvps);
    }

    @PutMapping("/rsvp")
    public ResponseEntity<RSVPDTO> updateRSVP(
            @RequestParam Long userId,
            @RequestParam Long eventId,
            @RequestBody RSVPDTO rsvpdto) {

        RSVPDTO rsvp = rsvpService.updateRSVP(userId, eventId, rsvpdto);
        return ResponseEntity.ok(rsvp);
    }

    @DeleteMapping("/rsvp")
    public ResponseEntity<String> deleteRSVP(
            @RequestParam Long userId,
            @RequestParam Long eventId,
            @RequestBody RSVPDTO rsvpdto) {

        rsvpService.deleteRSVP(userId, eventId, rsvpdto);
        return ResponseEntity.ok("Deleted RSVP successfully!");
    }
}
