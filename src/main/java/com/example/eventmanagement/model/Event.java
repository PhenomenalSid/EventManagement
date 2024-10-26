package com.example.eventmanagement.model;

import com.example.eventmanagement.dto.EventDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String location;
    private LocalDateTime date;
    private String description;

    @ManyToOne
    private User organizer;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL)
    private List<RSVP> rsvps = new ArrayList<>();

    public static EventDTO toDTO(Event event){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        return new EventDTO(event.getName(), event.getLocation(), event.getDate().format(formatter), event.getDescription(), event.getOrganizer().getUsername());
    }
}

