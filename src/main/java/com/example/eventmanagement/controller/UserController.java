package com.example.eventmanagement.controller;

import com.example.eventmanagement.dto.AuthDTO;
import com.example.eventmanagement.dto.UserDTO;
import com.example.eventmanagement.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/user/{userId}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long userId){
        UserDTO user = userService.getUserById(userId);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/user/{userId}")
    public ResponseEntity<UserDTO> updateUserById(@RequestBody AuthDTO authDTO, @PathVariable Long userId){
        UserDTO user = userService.updateUserById(authDTO, userId);
        return ResponseEntity.ok(user);
    }

    @DeleteMapping("/user/{userId}")
    public ResponseEntity<String> deleteUserById(@PathVariable Long userId){
        userService.deleteUserById(userId);
        return ResponseEntity.ok("Deleted user successfully!");
    }
}
