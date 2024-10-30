package com.example.eventmanagement.dto;

import com.example.eventmanagement.model.Event;
import com.example.eventmanagement.util.CacheableResource;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventDTO implements CacheableResource {
    @JsonIgnore
    Long id;

    String name;
    String location;
    private String date;
    private String description;
    private String organizer;

    public static Event toEntity(EventDTO eventDTO) {
        Event event = new Event();
        event.setName(eventDTO.getName());
        event.setLocation(eventDTO.getLocation());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        try {
            LocalDateTime dateTime = LocalDateTime.parse(eventDTO.getDate(), formatter);
            event.setDate(dateTime);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid date format. Please use 'dd-MM-yyyy HH:mm'");
        }
        event.setDescription(eventDTO.getDescription());
        return event;
    }
}
