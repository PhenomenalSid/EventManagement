package com.example.eventmanagement.model;

import com.example.eventmanagement.dto.UserDTO;
import com.example.eventmanagement.enums.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "Username", unique = true, nullable = false)
    private String username;

    @Column(name = "Email", unique = true, nullable = false)
    private String email;

    @Column(name = "Password", nullable = false)
    private String password;

    @Column(name = "Role", nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    @OneToMany(mappedBy = "organizer")
    private List<Event> events = new ArrayList<>();

    public static UserDTO toDTO(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setUsername(user.getUsername());
        userDTO.setEmail(user.getEmail());
        userDTO.setRole(user.getRole().toString());
        userDTO.setEvents(user.getEvents().stream().map(Event::toDTO).collect(Collectors.toList()));
        return userDTO;
    }
}

