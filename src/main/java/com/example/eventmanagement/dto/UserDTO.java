package com.example.eventmanagement.dto;

import com.example.eventmanagement.enums.Role;
import com.example.eventmanagement.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private String username;
    private String role;
    private List<EventDTO> events;

    public static User toEntity(UserDTO userDTO){
        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setRole(Role.fromString(userDTO.getRole()));
        return user;
    }
}
