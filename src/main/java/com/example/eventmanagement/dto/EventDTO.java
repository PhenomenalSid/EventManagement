package com.example.eventmanagement.dto;

import com.example.eventmanagement.model.Event;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventDTO {
    String name;
    String location;
    private String date;
    private String description;
    private String organizer;

    public static Event toEntity(EventDTO eventDTO){
        Event event = new Event();
        event.setName(eventDTO.getName());
        event.setLocation(eventDTO.getLocation());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        LocalDateTime dateTime = LocalDateTime.parse(eventDTO.getDate(), formatter);
        event.setDate(dateTime);
        event.setDescription(eventDTO.getDescription());
        return event;
    }
}
