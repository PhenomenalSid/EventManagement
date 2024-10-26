package com.example.eventmanagement.controller;

import com.example.eventmanagement.dto.AuthDTO;
import com.example.eventmanagement.dto.UserDTO;
import com.example.eventmanagement.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @GetMapping("/health-check")
    public ResponseEntity<String> hello() {
        return ResponseEntity.ok("Hello Project! Welcome to my guys!");
    }

    @PostMapping("/register")
    public ResponseEntity<UserDTO> registerUser(@RequestBody AuthDTO authDTO) {
        UserDTO user = userService.createUser(authDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    @PostMapping("/login")
    public ResponseEntity<String> loginUser(@RequestBody AuthDTO authDTO) {
        String jwt = userService.loginUser(authDTO);
        return ResponseEntity.ok(jwt);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logoutUser(@RequestHeader("Authorization") String token) {
        userService.logoutUser(token);
        return ResponseEntity.ok("Logged out successfully!");
    }
}
