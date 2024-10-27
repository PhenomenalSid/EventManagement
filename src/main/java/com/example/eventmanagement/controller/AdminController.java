package com.example.eventmanagement.controller;

import com.example.eventmanagement.dto.EventDTO;
import com.example.eventmanagement.dto.UserDTO;
import com.example.eventmanagement.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admins")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @GetMapping("/all-users")
    public ResponseEntity<List<UserDTO>> getAllUsers(){
        List<UserDTO> users = adminService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @PutMapping("/user/{userId}")
    public ResponseEntity<UserDTO> updateUserRole(@PathVariable Long userId, @RequestBody String role){
        UserDTO user = adminService.updateUserRole(userId, role);
        return ResponseEntity.ok(user);
    }

    @DeleteMapping("/user/{userId}")
    public ResponseEntity<String> deleteUser(@PathVariable Long userId){
        adminService.deleteUser(userId);
        return ResponseEntity.ok("Deleted User successfully!");
    }

    @GetMapping("/all-events")
    public ResponseEntity<List<EventDTO>> getAllEvents(){
        List<EventDTO> events = adminService.getAllEvents();
        return ResponseEntity.ok(events);
    }

    @DeleteMapping("/event/{eventId}")
    public ResponseEntity<String> deleteEvent(@PathVariable Long eventId){
        adminService.deleteEvent(eventId);
        return ResponseEntity.ok("Deleted Event successfully!");
    }
}
