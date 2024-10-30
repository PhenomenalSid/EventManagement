package com.example.eventmanagement.dto;

import com.example.eventmanagement.util.CacheableResource;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO implements CacheableResource {
    @JsonIgnore
    Long id;

    private String username;
    private String email;
    private String role;
    private List<EventDTO> events;
}
